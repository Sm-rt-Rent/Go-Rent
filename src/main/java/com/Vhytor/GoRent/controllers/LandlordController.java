package com.Vhytor.GoRent.controllers;

import com.Vhytor.GoRent.dtos.request.CreateHomeRequest;
import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.HomePhoto;
import com.Vhytor.GoRent.model.User;
import com.Vhytor.GoRent.model.ViewingRecord;
import com.Vhytor.GoRent.repositories.UserRepository;
import com.Vhytor.GoRent.services.HomeService;
import com.Vhytor.GoRent.services.LandlordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/landlord")
public class LandlordController {

    private final LandlordService landlordService;
    private final UserRepository userRepository;
    private final HomeService homeService;


    public LandlordController(LandlordService landlordService, UserRepository userRepository, HomeService homeService) {
        this.landlordService = landlordService;
        this.userRepository = userRepository;
        this.homeService = homeService;
    }

    @GetMapping("/properties")
    public ResponseEntity<List<Home>> getMyProperties() {
        User landlord = getCurrentUser();
        return ResponseEntity.ok(landlordService.getMyProperties(landlord));
    }

    @GetMapping("/properties/{homeId}/history")
    public ResponseEntity<List<ViewingRecord>> getViewingHistory(@PathVariable Long homeId) {
        User landlord = getCurrentUser();
        return ResponseEntity.ok(landlordService.getMyViewingRecords(homeId, landlord));
    }
    @PostMapping("/properties")
    public ResponseEntity<Home> createProperty(@Valid @RequestBody CreateHomeRequest request) {
        User landlord = getCurrentUser();
        Home createdProperty = landlordService.createProperty(request, landlord);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProperty);
    }

    /**
     * POST /api/landlord/properties/{homeId}/photos
     * Uploads one or more photos for a property (multipart/form-data).
     * Field name must be "files" — supports multiple files in one request.
     */
    @PostMapping(value = "/properties/{homeId}/photos", consumes = "multipart/form-data")
    public ResponseEntity<List<HomePhoto>> uploadPhotos(
            @PathVariable Long homeId,
            @RequestParam("files") List<MultipartFile> files,
            Principal principal) {
        User landlord = resolveUser(principal);
        List<HomePhoto> uploaded = homeService.uploadPhotos(homeId, files, landlord);
        return ResponseEntity.status(HttpStatus.CREATED).body(uploaded);
    }

    /**
     * DELETE /api/landlord/properties/{homeId}/photos/{photoId}
     * Removes a single photo from the property.
     */
    @DeleteMapping("/properties/{homeId}/photos/{photoId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long homeId,
            @PathVariable Long homePhotoId,
            Principal principal) {
        User landlord = resolveUser(principal);
        homeService.deletePhoto(homeId, homePhotoId, landlord);
        return ResponseEntity.noContent().build();
    }

    private User resolveUser(Principal principal) {
        return userRepository.findByUserEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}