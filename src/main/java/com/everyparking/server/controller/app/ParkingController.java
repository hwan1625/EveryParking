package com.everyparking.server.controller.app;

import static org.springframework.http.ResponseEntity.status;

import com.everyparking.server.data.dto.CarDto;
import com.everyparking.server.data.dto.MemberDto;
import com.everyparking.server.data.dto.ParkingDto;
import com.everyparking.server.data.dto.ParkingDto.MyParkingStatus;
import com.everyparking.server.data.dto.ParkingDto.ParkingLotMap;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.ParkingLot;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.event.EntryLogChangeEvent;
import com.everyparking.server.exception.ParkingInfoException;
import com.everyparking.server.service.ParkingService;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Slf4j

@RequestMapping("/app/parking")
public class ParkingController {

    private final ParkingService parkingService;
    private final ApplicationEventPublisher eventPublisher;

    private final MemberRepository memberRepository;


    /**
     * 메인화면 - 내 주차 정보
     *
     * @param request
     * @return
     */
    @GetMapping("/myParkingStatus")
    public ResponseEntity<?> myParkingStatus(HttpServletRequest request) {
        String userId = request.getHeader("userId").toString();
        log.info("[ParkingController] userId : {}", userId);
        HashMap<String, String> response = new HashMap<>();

        /*TODO 남은 시간 계산 로직 추가*/

        try {
            MyParkingStatus result = parkingService.findByUserId(userId);
            log.info("[{}] {}", this.getClass().getName(), result.toString());

//            if (result == null) {
//                return status(HttpStatus.NOT_FOUND).body("자리 배정 필요");
//            } else{
//                return status(HttpStatus.OK)
//                    .body(result);
//            }

            return status(HttpStatus.OK).body(result);

        } catch (ParkingInfoException e) {
            log.info("[{}] {}", this.getClass().getName(), e.toString());
            response.put("message", e.toString());
            return status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            log.info("[ParkingController] {}", e.toString());
            response.put("message", e.toString());

            return status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    /**
     * 주차 맵
     */
    @GetMapping("/map/{parkingLotId}")
    public ResponseEntity<ParkingDto.ParkingLotMap> parkingLotMap(@PathVariable Long parkingLotId) {

        log.info("[ParkingController] parkingLotId : {}", parkingLotId);

        try {
            /*ParkingLot 조회*/
            ParkingLot parkingLot = parkingService.findParkingLotByParkingLotId(parkingLotId);
            ParkingLotMap parkingLotMap = parkingService.findParkingLotMap(parkingLot);

            return status(HttpStatus.OK)
                    .body(parkingLotMap);

        } catch (Exception e) {
            log.info("[ParkingController] {}", e.toString());

            return status(HttpStatus.NOT_FOUND)
                    .build();
        }
    }

    /**
     * 자리 정보
     *
     * @param parkingInfoId
     * @return
     */
    @GetMapping("/info/{parkingInfoId}")
    public ResponseEntity<?> parkingInfo(@PathVariable Long parkingInfoId) {

        try {
            return status(HttpStatus.OK)
                    .body(
                            parkingService.findByParkingId(parkingInfoId));
        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());

            return status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /**
     * 차량 자리 배정
     *
     * @param parkingInfoId
     * @param request
     * @return
     */
    @GetMapping("/assign/{parkingInfoId}")
    public ResponseEntity<?> assign(@PathVariable Long parkingInfoId, HttpServletRequest request) {

        try {
            ParkingDto.ParkingInfoDto.Info parkingInfo = parkingService.assign(parkingInfoId, request.getHeader("userId"));

            /*관리자 실시간 맵 렌더링*/
            Member member = memberRepository.findByParkingInfoId(parkingInfoId).orElseThrow(
                    () -> new Exception("현재 유저는 자리를 배정 받은 상태가 아닙니다.")
            );
            eventPublisher.publishEvent(new EntryLogChangeEvent(
                    ParkingDto.ParkingInfoDto.Info.builder()
                            .id(parkingInfo.getId())
                            .parkingId(parkingInfo.getParkingId())
                            .parkingStatus(parkingInfo.getParkingStatus())
                            .details(
                                    CarDto.ParkingInfo.builder()
                                            .id(member.getCar().getId())
                                            .carNumber(member.getCar().getCarNumber())
                                            .member(
                                                    MemberDto.UserParkingInfo.builder()
                                                            .id(member.getId())
                                                            .userId(member.getUserId())
                                                            .userName(member.getUserName())
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            ));

            return status(HttpStatus.OK)
                    .body(parkingInfo);

        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());

            return status(HttpStatus.NOT_FOUND).build();

        }

    }

    /*반납*/
    @GetMapping("/return/{parkingInfoId}")
    public ResponseEntity<?> parkingReturn(@PathVariable Long parkingInfoId,
                                           HttpServletRequest request) {

        try {
            parkingService.parkingReturn(parkingInfoId, request.getHeader("userId"));

            /*관리자 실시간 맵 렌더링*/
            eventPublisher.publishEvent(new EntryLogChangeEvent(parkingService.findByParkingId(parkingInfoId)));

            return status(HttpStatus.OK).build();
        } catch (Exception e) {
            return status(HttpStatus.BAD_REQUEST).build();
        }

    }


}