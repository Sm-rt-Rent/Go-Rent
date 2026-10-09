package com.Vhytor.GoRent.repositories;

import com.Vhytor.GoRent.model.HomePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomePhotoRepository extends JpaRepository<HomePhoto, Long> {

    List<HomePhoto> findByHomeHomeIdOrderByDisplayOrderAsc(Long homeId);

    long countByHomeHomeId(Long homeId);
}
