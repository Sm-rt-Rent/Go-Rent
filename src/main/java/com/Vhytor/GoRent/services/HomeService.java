package com.Vhytor.GoRent.services;

import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.HomePhoto;
import com.Vhytor.GoRent.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HomeService {

    List<Home> getAllHomes();
    Home getHomeById(Long homeId);

    /**
     * Searches for properties within a given radius of a coordinate.
     * Used by the map search feature on the tenant dashboard.
     *
     * @param lat          latitude of the searched location
     * @param lng          longitude of the searched location
     * @param radiusMetres search radius in metres
     */
    List<Home> searchNearby(double lat, double lng, double radiusMetres);

    /**
     * Uploads one or more photos for a property and attaches them to it.
     * Only the landlord who owns the property can upload photos to it.
     *
     * @param homeId  The property to attach photos to
     * @param files   The image files to upload
     * @param landlord The landlord making the request (for ownership check)
     * @return The list of newly created HomePhoto records
     */
    List<HomePhoto> uploadPhotos(Long homeId, List<MultipartFile> files, User landlord);

    /**
     * Deletes a single photo from both Cloudinary and the database.
     * Only the landlord who owns the property can delete its photos.
     */
    void deletePhoto(Long homeId, Long homePhotoId, User landlord);
}
