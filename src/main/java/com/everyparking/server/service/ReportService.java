package com.everyparking.server.service;

import com.everyparking.server.data.dto.ReportDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ReportService {


    void uploadReport(String userId, ReportDto reportDto, MultipartFile multipartFile);
}
