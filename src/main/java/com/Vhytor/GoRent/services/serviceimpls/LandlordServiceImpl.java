package com.Vhytor.GoRent.services.serviceimpls;

import com.Vhytor.GoRent.dtos.request.CreateHomeRequest;
import com.Vhytor.GoRent.exceptions.PropertyHasViewingException;
import com.Vhytor.GoRent.exceptions.PropertyNotFoundException;
import com.Vhytor.GoRent.exceptions.UnauthorizedAccessException;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.HomePhoto;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.model.ViewingRecord;
import com.Vhytor.GoRent.repositories.HomeRepository;
import com.Vhytor.GoRent.repositories.ViewingRecordRepository;
import com.Vhytor.GoRent.services.LandlordService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
public class LandlordServiceImpl implements LandlordService {

    private static final Logger log = LoggerFactory.getLogger(LandlordServiceImpl.class);
    private final HomeRepository homeRepository;
    private final ViewingRecordRepository viewingRecordRepository;
    private final Cloudinary cloudinary;

    public LandlordServiceImpl(HomeRepository homeRepository, ViewingRecordRepository viewingRecordRepository, Cloudinary cloudinary) {
        this.homeRepository = homeRepository;
        this.viewingRecordRepository = viewingRecordRepository;
        this.cloudinary = cloudinary;
    }
    @Override
//    @Cacheable(value = "landlord-properties", key = "#landlord.userId")
    @Transactional(readOnly = true)
    public List<Home> getMyProperties(User landlord) {
        // We'll need to add findByLandlord to HomeRepository
        List<Home> homes = homeRepository.findByLandlord_UserId(landlord.getUserId()); // Or whatever your repository method is

        // Force initialization of lazy photos collection while session is open
        homes.forEach(home -> {
            Hibernate.initialize(home.getPhotos());
            Hibernate.initialize(home.getLandlord());
        });

        return homes;
    }

    @Override
    @Cacheable(value = "viewing-records", key = "#homeId")
    public List<ViewingRecord> getMyViewingRecords(long homeId, User landlord) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new PropertyNotFoundException(homeId));

        if (!home.getLandlord().getUserId().equals(landlord.getUserId())) {
            throw new UnauthorizedAccessException("You do not own this property" + homeId);
        }
        return viewingRecordRepository.findByHomeHomeId(homeId);
    }

    @Override
    @CacheEvict(value = {"landlord-properties", "all-homes"}, allEntries = true)
    public Home createProperty(CreateHomeRequest request, User landlord) {
        Home home = new Home();
        home.setLandlord(landlord);
        home.setAddress(request.getAddress());
        home.setDescription(request.getDescription());
        home.setPricePerMonth(request.getPricePerMonth());
        home.setViewingFee(request.getViewingFee());
        home.setLatitude(request.getLatitude());
        home.setLongitude(request.getLongitude());
        return homeRepository.save(home);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"landlord-properties", "all-homes", "home", "viewing-records"}, allEntries = true)
    public void deleteProperty(Long homeId, User landlord) {
        Home home = homeRepository.findById(homeId)
                .orElseThrow(() -> new PropertyNotFoundException(homeId));

        if (!home.getLandlord().getUserId().equals(landlord.getUserId())) {
            throw new UnauthorizedAccessException(
                    "You do not own property with ID " + homeId);
        }

        if (viewingRecordRepository.existsByHomeHomeIdAndPaidTrue(homeId)) {
            throw new PropertyHasViewingException(homeId);
        }

        // Capture Cloudinary IDs before the rows are gone
        List<String> photoPublicIds = home.getPhotos().stream()
                .map(HomePhoto::getPublicId)
                .toList();

        viewingRecordRepository.deleteByHomeHomeId(homeId);
        homeRepository.delete(home);
        homeRepository.flush();

        for (String publicId : photoPublicIds) {
            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                log.warn("Property {} deleted, but Cloudinary asset {} could not be removed: {}",
                        homeId, publicId, e.getMessage());
            }
        }
    }
}
