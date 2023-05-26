package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.EntryLogDto;
import com.everyparking.server.data.dto.ParkingBreakerDto;
import com.everyparking.server.data.dto.ParkingBreakerDto.Request;
import com.everyparking.server.data.entity.*;
import com.everyparking.server.data.repository.CarRepository;
import com.everyparking.server.data.repository.EntryLogRepository;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.data.repository.ParkingInfoRepository;
import com.everyparking.server.event.EntryLogChangeEvent;
import com.everyparking.server.exception.CarValidationException;
import com.everyparking.server.service.ParkingBreakerService;
import com.everyparking.server.service.ParkingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional()
public class ParkingBreakerServiceImpl implements ParkingBreakerService {

    private final ParkingInfoRepository parkingInfoRepository;

    private final CarRepository carRepository;

    private final EntryLogRepository entryLogRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    private final ParkingService parkingService;

    @Override
    public ParkingBreakerDto.Response isValid(Request request) {
        String plateNumber = request.getPlateNumber();
        int parkingLotId = request.getParkingLotId();

        try {
            /*출입 가능 차량 조회*/
            Car findCar = carRepository.findByCarNumber(plateNumber).orElseThrow(
                () -> new CarValidationException("미등록 차량")
            );

            if (!findCar.getMember().checkMemberStatus()) {
                throw new Exception("위약된 사용자");
            }

            if (findCar.getCarEnterStatus().isEnter()) {
                throw new Exception("해당 차량은 이미 들어와 있습니다.");
            }

            /*TODO 위약 검증 로직 추가*/

            /* 주차장 출입 처리*/
            try {
                findCar.setEnter(parkingLotId);
                carRepository.save(findCar);

            } catch (Exception e) {
                log.debug("[ParkingBreakerService] {}", e.toString());

            }

            log.info("[ParkingBreakerService] {} 등록 차량", plateNumber);
            log.info("[ParkingBreakerService] {}, {}번 주차장 출입", findCar.getCarNumber(),
                findCar.getCarEnterStatus().getParkingLotId());

            log.debug("[ParkingBreakerService] {}", findCar.getCarEnterStatus().toString());
//            log.debug("[{}]", this.getClass().getName().toString());

            return ParkingBreakerDto.Response
                .builder()
                .valid(true)
                .build();

        } catch (CarValidationException e) {
            log.debug("[ParkingBreakerService] {}", e.toString());
            log.info("[ParkingBreakerService] {} 미등록 차량", plateNumber);
            return ParkingBreakerDto.Response
                .builder()
                .valid(false)
                .build();
        } catch (Exception e) {
            log.debug("[ParkingBreakerService] {}", e.toString());
            log.info("[ParkingBreakerService] {} 위약처리된 차량", plateNumber);
            return ParkingBreakerDto.Response
                .builder()
                .valid(false)
                .build();
        }

    }
    @Override
    public void entry(String fileFullName) {
        try {
            // 이미지 파일명 split 해서 입력
            int dotIndex = fileFullName.lastIndexOf(".");
            String vehicle_info = (dotIndex == -1) ? fileFullName : fileFullName.substring(0, dotIndex);
            String[] info = vehicle_info.split("_");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
            LocalDateTime localDateTime = LocalDateTime.parse(info[1], formatter);
            EntryLog entryLogEntity = EntryLog.builder()
                    .carNumber(info[0])
                    .entryTime(localDateTime)
                    .build();
            EntryLog savedEntity = entryLogRepository.save(entryLogEntity);

            EntryLogDto entryLogDto = EntryLogDto.builder()
                    .id(savedEntity.getId())
                    .carNumber(savedEntity.getCarNumber())
                    .entryTime(savedEntity.getEntryTime())
                    .exitTime(savedEntity.getExitTime())
                    .build();
            eventPublisher.publishEvent(new EntryLogChangeEvent(entryLogDto));
        } catch(Exception e) {
            log.debug("[ParkingBreakerService] {}", e.toString());
        }
    }

    @Override
    public void exit(String carNumber) {
        // exitTime update
        // 해당 차량에 대한 출입 기록 중에 출차 기록이 없으면 exitTime 갱신
        try {
            Optional<EntryLog> found = entryLogRepository.findFirstByCarNumberAndExitTimeIsNull(carNumber);
            if(found.isEmpty()) {
                throw new Exception("들어온 기록이 없음.");
            }
            EntryLog updated = found.get();
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            updated.setExitTime(zonedDateTime.toLocalDateTime());
            entryLogRepository.save(updated);
            eventPublisher.publishEvent(new EntryLogChangeEvent(updated.toDto()));

            // car is_entered 업데이트
            // Car 엔티티 @Setter 추가
            Optional<Car> found2 = carRepository.findByCarNumber(carNumber);
            if (found2.isEmpty()) {
                throw new Exception("error");
            }
            Car updated2 = found2.get();
            updated2.setCarEnterStatus(new CarEnterStatus(-1, false));
            carRepository.save(updated2);

            /*member parking info update*/
            Optional<Member> member = memberRepository.findByCarId(updated2.getId());
            if (member.isPresent()) {
                Member foundMember = member.get();
                Long parkingInfoId = foundMember.getParkingInfo().getId();
                if (parkingInfoId != null) {
                    foundMember.changeParkingStatus(parkingInfoRepository.findById(foundMember.getParkingInfo().getId()).get());
                    memberRepository.save(foundMember);
                    /*관리자 실시간 맵 렌더링*/
                    eventPublisher.publishEvent(new EntryLogChangeEvent(parkingService.findByParkingId(parkingInfoId)));
                }
            }


        } catch(Exception e) {
            log.debug("[ParkingBreakerService] {}", e.toString());
        }
    }

}
