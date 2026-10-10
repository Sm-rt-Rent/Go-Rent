package com.Vhytor.GoRent.repositories;

import com.Vhytor.GoRent.model.Home;
import com.Vhytor.GoRent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

    List<Home> findByLandlord(User landlord);
    List<Home> findByLandlord_UserId(Long userId);

    /**
     * Finds all properties within a given radius (in metres) of a coordinate.
     *
     * Uses the Haversine formula to calculate the great-circle distance
     * between two points on Earth given their lat/lng coordinates.
     *
     * 6371000 = Earth's radius in metres
     *
     * Three levels of results come from calling this with different radii:
     *   Exact match:       radius = 100m
     *   Nearby match:      radius = 1000m  (1km — same neighbourhood)
     *   Wide area match:   radius = 5000m  (5km — same district)
     */
    @Query("""
        SELECT h FROM Home h
        WHERE h.latitude IS NOT NULL
          AND h.longitude IS NOT NULL
          AND (6371000 * acos(
                cos(radians(:lat)) * cos(radians(h.latitude)) *
                cos(radians(h.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(h.latitude))
              )) <= :radiusMetres
        ORDER BY (6371000 * acos(
                cos(radians(:lat)) * cos(radians(h.latitude)) *
                cos(radians(h.longitude) - radians(:lng)) +
                sin(radians(:lat)) * sin(radians(h.latitude))
              )) ASC
    """)
    List<Home> findWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMetres") double radiusMetres
    );


}
