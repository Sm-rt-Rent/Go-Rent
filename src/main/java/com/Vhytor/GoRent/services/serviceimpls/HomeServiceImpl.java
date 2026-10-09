package com.Vhytor.GoRent.services.serviceimpls;

import com.Vhytor.GoRent.exceptions.PhotoUploadException;
import com.Vhytor.GoRent.exceptions.PropertyNotFoundException;
import com.Vhytor.GoRent.exceptions.UnauthorizedAccessException;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.HomePhoto;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.repositories.HomePhotoRepository;
import com.Vhytor.GoRent.repositories.HomeRepository;
import com.Vhytor.GoRent.services.HomeService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {
    private final HomeRepository homeRepository;
    private final HomePhotoRepository homePhotoRepository;
    private final Cloudinary cloudinary;

    // Keeps listings fast to load — more than this and the gallery gets unwieldy
    private static final int MAX_PHOTOS_PER_HOME = 10;

    public HomeServiceImpl(HomeRepository homeRepository, HomePhotoRepository homePhotoRepository , Cloudinary cloudinary) {
        this.homeRepository = homeRepository;
        this.homePhotoRepository = homePhotoRepository;
        this.cloudinary = cloudinary;

    }


    @Override
    @Cacheable(value = "all-homes")
    @Transactional(readOnly = true)
    public List<Home> getAllHomes() {
        List<Home> homes = homeRepository.findAll();
        homes.forEach(home -> Hibernate.initialize(home.getPhotos()));
        return homes;
    }

    @Override
    @Cacheable(value = "home", key = "#homeId")
    @Transactional(readOnly = true)
    public Home getHomeById(Long homeId) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(()-> new PropertyNotFoundException(homeId));
        Hibernate.initialize(home.getPhotos());
        return home;
    }

    /**
     * Returns all properties within the given radius of a coordinate.
     * Results are ordered by distance — closest first.
     * Not cached since coordinates change per search.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Home> searchNearby(double lat, double lng, double radiusMetres) {
        List<Home> homes = homeRepository.findWithinRadius(lat, lng, radiusMetres);
        homes.forEach(home -> Hibernate.initialize(home.getPhotos()));
        return homes;
    }

    /**
     * Uploads each file to Cloudinary, then saves a HomePhoto record
     * for each successful upload. Enforces ownership and a max-photo limit.
     *
     * Cache for "all-homes" and this specific home is evicted so the
     * new photos appear immediately without waiting for TTL expiry.
     */
    @Override
    //@CacheEvict(value = {"all-homes", "home"}, key = "#homeId", allEntries = false)
    @CacheEvict(value = {"landlord-properties", "all-homes"}, allEntries = true)
    public List<HomePhoto> uploadPhotos(Long homeId, List<MultipartFile> files, User landlord) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new PropertyNotFoundException(homeId));

        if (!home.getLandlord().getUserId().equals(landlord.getUserId())) {
            throw new UnauthorizedAccessException(
                    "You do not own property with ID " + homeId);
        }

        long existingCount = homePhotoRepository.countByHomeHomeId(homeId);
        if (existingCount + files.size() > MAX_PHOTOS_PER_HOME) {
            throw new PhotoUploadException(
                    "A property can have at most " + MAX_PHOTOS_PER_HOME + " photos. " +
                            "This property already has " + existingCount + ".");
        }

        List<HomePhoto> uploaded = new ArrayList<>();
        int nextOrder = (int) existingCount;

        for (MultipartFile file : files) {
            validateImageFile(file);

            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> result = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", "gorent/properties/" + homeId,
                                "resource_type", "image"
                        )
                );

                HomePhoto photo = new HomePhoto();
                photo.setHome(home);
                photo.setUrl((String) result.get("secure_url"));
                photo.setPublicId((String) result.get("public_id"));
                photo.setDisplayOrder(nextOrder++);

                uploaded.add(homePhotoRepository.save(photo));

            } catch (IOException e) {
                throw new PhotoUploadException(
                        "Failed to upload " + file.getOriginalFilename(), e);
            }
        }

        return uploaded;
    }

    /**
     * Removes a photo from Cloudinary first, then deletes the DB record.
     * If Cloudinary deletion fails we still remove the DB record so the
     * listing doesn't show a broken reference — the orphaned Cloudinary
     * asset can be cleaned up later via Cloudinary's dashboard.
     */
    @Override
    @CacheEvict(value = {"landlord-properties", "all-homes"}, allEntries = true)
    public void deletePhoto(Long homeId, Long homePhotoId, User landlord) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new PropertyNotFoundException(homeId));

        if (!home.getLandlord().getUserId().equals(landlord.getUserId())) {
            throw new UnauthorizedAccessException(
                    "You do not own property with ID " + homeId);
        }

        HomePhoto photo = homePhotoRepository.findById(homePhotoId)
                .orElseThrow(() -> new PhotoUploadException("Photo not found with ID " + homePhotoId));

        if (!photo.getHome().getHomeId().equals(homeId)) {
            throw new PhotoUploadException("Photo does not belong to this property.");
        }

        try {
            cloudinary.uploader().destroy(photo.getPublicId(), ObjectUtils.emptyMap());
        } catch (IOException e) {
            // Log and continue — don't block DB cleanup over a Cloudinary hiccup
            System.err.println("Failed to delete Cloudinary asset " + photo.getPublicId() + ": " + e.getMessage());
        }

        homePhotoRepository.delete(photo);
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new PhotoUploadException("One of the uploaded files is empty.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new PhotoUploadException(
                    file.getOriginalFilename() + " is not a valid image file.");
        }

        // 5MB limit per photo — keeps uploads fast and Cloudinary free tier friendly
        long maxSizeBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new PhotoUploadException(
                    file.getOriginalFilename() + " exceeds the 5MB size limit.");
        }
    }
}
