package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.EntryLog;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.Report;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByOrderByCreatedTimeDesc();

}
