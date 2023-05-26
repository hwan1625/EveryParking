package com.everyparking.server.data.repository;

import com.everyparking.server.filestore.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
    UploadFile findByReportId(Long reportId);

}
