package com.everyparking.server.controller.web;

import com.everyparking.server.data.dto.*;
import com.everyparking.server.data.entity.ParkingLot;
import com.everyparking.server.service.ManagerWebService;
import com.everyparking.server.service.ParkingBreakerService;
import com.everyparking.server.service.ParkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.ResponseEntity.status;

@RequiredArgsConstructor
@Slf4j
@RestController
public class ManagerWebController {
    private final ManagerWebService managerWebService;
    private final ParkingBreakerService parkingBreakerService;
    private final ParkingService parkingService;

    @GetMapping("/api/entry-log")
    public List<EntryLogDto> getEntryLogs(@PageableDefault(size=20) Pageable pageable) {
        return managerWebService.getAllEntryLogs(pageable).getContent();
    }

    @GetMapping("/api/exit")
    public ResponseEntity<?> setExitLog(@RequestParam("number") String carNumber) {
        try {
            parkingBreakerService.exit(carNumber);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/api/map/{parkingLotId}")
    public ResponseEntity<List<ParkingDto.ParkingInfoDto.Info>> parkingLotMap(@PathVariable Long parkingLotId) {
        try {
            /*ParkingLot 조회*/
            ParkingLot parkingLot = parkingService.findParkingLotByParkingLotId(
                    parkingLotId);
            ParkingDto.ParkingLotMap parkingLotMap = parkingService.findParkingLotMap(parkingLot);

            /*각 좌석마다 차량정보와 차량정보*/
            List<ParkingDto.ParkingInfoDto.Info> assignInfo = new ArrayList<>();
            parkingLotMap.getParkingInfoList().forEach(info -> {
                assignInfo.add(parkingService.findByParkingId(info.getId()));
            });

            return status(HttpStatus.OK)
                    .body(assignInfo);

        } catch (Exception e) {
            return status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    /**
     * 신고리스트 전체 불러오기. 최근 시간 순으로 정렬
     */
    @GetMapping("/api/report-log")
    public List<ReportItemDto> getReportLogs() {
        return managerWebService.getAllReportLogs();
    }

    /**
     * 관리자 전용 회원 위약 처리
     */
    @GetMapping("/api/violation")
    public ResponseEntity<?> violation(HttpServletRequest request) {
        try {
            String userId = request.getHeader("userId");
            if (userId == null) {
                throw new Exception("해당 정보로 위약할 수 없습니다.");
            }
            managerWebService.violation(userId);
            return ResponseEntity.status(HttpStatus.OK).body("User violated Successfully!");
        } catch (Exception e) {
            log.info("[ManagerWebController] {}", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to violate user");
        }
    }

    @GetMapping("/api/report-cancel")
    public ResponseEntity<?> reportCancel(HttpServletRequest request) {
        try {
            Long reportId = Long.valueOf(request.getHeader("reportId"));
            if (reportId == null) {
                throw new Exception("해당 정보로 신고를 취소할 수 없습니다.");
            }
            managerWebService.reportCancel(reportId);
            return ResponseEntity.status(HttpStatus.OK).body("Report cancel Successfully!");
        } catch (Exception e) {
            log.info("[ManagerWebController] {}", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to cancel report");
        }
    }

    @PostMapping("/api/search")
    public ResponseEntity<?> search(@RequestBody SearchDto searchDto) {
        try {
            if (searchDto.getValue() == null) {
                throw new Exception("해당 정보로 조회할 수 없습니다.");
            }
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(managerWebService.searchUser(searchDto.getValue(), searchDto.getOption()));
        } catch (Exception e) {
            log.info("[ManagerWebController] {}", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to search");
        }
    }

    @GetMapping("/api/user/violation")
    public ResponseEntity<?> userViolationRegister(HttpServletRequest request) {
        try {
            managerWebService.userViolation(request.getHeader("userId"));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("violation successfully");
        } catch (Exception e) {
            log.info("[ManagerWebController] {}", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to search");
        }
    }

    @GetMapping("/api/user/unviolation")
    public ResponseEntity<?> userUnViolationRegister(HttpServletRequest request) {
        try {
            managerWebService.userUnViolation(request.getHeader("userId"));
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body("unviolation successfully");
        } catch (Exception e) {
            log.info("[ManagerWebController] {}", e.toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to search");
        }
    }
}
