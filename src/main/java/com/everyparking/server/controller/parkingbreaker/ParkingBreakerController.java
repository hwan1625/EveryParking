package com.everyparking.server.controller.parkingbreaker;

import com.everyparking.server.data.dto.ParkingBreakerDto;
import com.everyparking.server.data.dto.ParkingBreakerDto.Response;
import com.everyparking.server.service.ParkingBreakerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@RestController
/**
 *
 * /api/vehicle_validation
 * {
 *      "plateNumber": "65노0887"
 * } -> {"isValid": true}
 */

@AllArgsConstructor
@Slf4j
@RequestMapping("/api/parking_breaker")
public class ParkingBreakerController {

    private final ParkingBreakerService parkingBreakerService;

    @PostMapping("/isValid")
    public ResponseEntity<?> isValid(@RequestBody ParkingBreakerDto.Request request) {
        log.info("[ParkingBreakerController] {}", request.toString());

        Response isValid = parkingBreakerService.isValid(request);

        if (isValid.isValid()) {

            return ResponseEntity.status(HttpStatus.OK).body(isValid);
        } else {
//            log.info("[ParkingBreakerController] {}", isValid.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(isValid);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @Value("${file.dir}") String dir,
            @RequestParam("image") MultipartFile file) {
        // 해당 차량이 입장 상태이면 예외처리

        try {
            String fileFullName = Objects.requireNonNull(file.getOriginalFilename());

            // 이미지 저장 경로 설정
            String uploadDir = dir;
            File uploadDirFile = new File(uploadDir);

            // 폴더 생성
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }

            // 이미지 파일 경로 설정
            File dest = new File(uploadDir, fileFullName);

            // 이미지 저장
            file.transferTo(dest);

            // DB 삽입 및 이벤트
            parkingBreakerService.entry(fileFullName);

            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file.");
        }
    }
}
