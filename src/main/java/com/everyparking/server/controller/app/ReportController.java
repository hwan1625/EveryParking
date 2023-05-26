package com.everyparking.server.controller.app;

import com.everyparking.server.data.dto.ReportDto;
import com.everyparking.server.service.ReportService;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    @PostMapping(value = "/upload", consumes = {MediaType.APPLICATION_JSON_VALUE,
        MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> upload(
        @RequestPart(name = "data") ReportDto reportDto,
        @RequestPart(name = "imageFile") MultipartFile imageFile, HttpServletRequest request) {

        String userId = request.getHeader("userId");

        try {
            reportService.uploadReport(userId, reportDto, imageFile);

            return ResponseEntity.status(HttpStatus.OK).build();

        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(),
                e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
