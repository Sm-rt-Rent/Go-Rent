package com.Vhytor.GoRent.repositories;

import com.Vhytor.GoRent.model.ViewingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewingRecordRepository extends JpaRepository<ViewingRecord, Long> {
    List<ViewingRecord> findByHomeHomeIdAndAccessCode(Long homeId, String code);
    List<ViewingRecord> findByHomeHomeId(Long homeId);

    Optional<ViewingRecord> findByTransactionReference(String transactionReference);

    boolean existsByHomeHomeIdAndPaidTrue(Long homeId);

    void deleteByHomeHomeId(Long homeId);
}
