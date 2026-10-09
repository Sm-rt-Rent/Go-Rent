package com.Vhytor.GoRent.controllers;

import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.services.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homes")
public class HomeController {
       HomeService homeService;

       public HomeController(HomeService homeService) {
           this.homeService = homeService;
       }

       @GetMapping("/{homeId}")
       public ResponseEntity<Home> getHomeById(@PathVariable Long homeId) {
           return ResponseEntity.ok(homeService.getHomeById(homeId));
       }
       @GetMapping
       public ResponseEntity<List<Home>> getAllHomes() {
           return ResponseEntity.ok(homeService.getAllHomes());
       }
    /**
     * GET /api/homes/search?lat=6.5244&lng=3.3792&radius=100
     *
     * Searches for properties near a coordinate.
     * radius is in metres — defaults to 5000m (5km) if not provided.
     *
     * The frontend calls this three times with increasing radii:
     *   100m   → exact match
     *   1000m  → nearby / same neighbourhood
     *   5000m  → same district / wide area
     */
    @GetMapping("/search")
    public ResponseEntity<List<Home>> searchNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5000") double radius) {
        return ResponseEntity.ok(homeService.searchNearby(lat, lng, radius));
    }
}
