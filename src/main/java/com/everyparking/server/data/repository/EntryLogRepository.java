package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.EntryLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntryLogRepository extends JpaRepository<EntryLog, Long> {
    Optional<EntryLog> findFirstByCarNumberAndExitTimeIsNull(String carNumber);
    Page<EntryLog> findAllByOrderByEntryTimeDesc(Pageable pageable);
}
