package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.CarDto;
import com.everyparking.server.data.dto.MemberDto.UserParkingInfo;
import com.everyparking.server.data.dto.ParkingDto;
import com.everyparking.server.data.dto.ParkingDto.MyParkingStatus;
import com.everyparking.server.data.dto.ParkingDto.ParkingInfoDto;
import com.everyparking.server.data.dto.ParkingDto.ParkingInfoDto.Info;
import com.everyparking.server.data.dto.ParkingDto.ParkingInfoDto.Map;
import com.everyparking.server.data.dto.ParkingDto.ParkingLotMap;
import com.everyparking.server.data.entity.Car;
import com.everyparking.server.data.entity.CarEnterStatus;
import com.everyparking.server.data.entity.EntryLog;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.MemberStatus;
import com.everyparking.server.data.entity.ParkingInfo;
import com.everyparking.server.data.entity.ParkingLot;
import com.everyparking.server.data.entity.ParkingStatus;
import com.everyparking.server.data.repository.CarRepository;
import com.everyparking.server.data.repository.EntryLogRepository;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.data.repository.ParkingInfoRepository;
import com.everyparking.server.data.repository.ParkingLotRepository;
import com.everyparking.server.event.EntryLogChangeEvent;
import com.everyparking.server.exception.ParkingInfoException;
import com.everyparking.server.exception.ParkingLotException;
import com.everyparking.server.exception.UserNotFoundException;
import com.everyparking.server.service.ParkingService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class ParkingServiceImpl implements ParkingService {

    private final ParkingLotRepository parkingLotRepository;

    private final ParkingInfoRepository parkingInfoRepository;

    private final MemberRepository memberRepository;
    private final EntryLogRepository entryLogRepository;
    private final CarRepository carRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * <p>
     * 사용자 주차 정보
     * </p>
     * <p>
     * uerId로 MyParkingStatus 검색
     *
     * @param userId
     * @return ParkingDto.MyParkingStatus
     *
     * </p>
     */
    @Override
    public ParkingDto.MyParkingStatus findByUserId(String userId) {

        Member member = memberRepository.findByUserId(userId).orElseThrow(
            () -> new UserNotFoundException("사용자 정보 없음")
        );

        if (!member.checkParkingStatus()) {
            throw new ParkingInfoException("자리를 먼저 배정하세요");
        } else {
            ParkingInfo parkingInfo = member.getParkingInfo();


            /*TODO 시간 처리 로직 추가*/
            return MyParkingStatus.builder()
                .parkingId(parkingInfo.getParkingId())
                .remain(1234)
                .carNumber(member.getCar().getCarNumber())
                .build();

        }

//
    }

    /*ParkingLotId로 ParkingLot 조회*/
    @Override
    public ParkingLot findParkingLotByParkingLotId(Long parkingLotId) {
        try {
            ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId).orElseThrow(
                () -> new ParkingLotException("일치하는 주차장 없음")
            );

            return parkingLot;

        } catch (Exception e) {
            log.info("[ParkingService] {}", e.toString());
            throw e;
        }
    }

    /*ParkingLot ->  ParkingMap 반환*/
    @Override
    public ParkingDto.ParkingLotMap findParkingLotMap(ParkingLot parkingLot) {
        try {
            List<ParkingInfo> parkingInfoList = parkingInfoRepository.findAllByParkingLot(
                    parkingLot)
                .orElseThrow(
                    () -> new ParkingLotException("일치하는 ParkingInfo 없음")
                );

            List<Map> parkingInfoDtoList = parkingInfoList.stream()
                .map(o -> Map.builder()
                    .id(o.getId())
                    .parkingId(o.getParkingId())
                    .parkingStatus(o.getParkingStatus())
                    .build()
                ).collect(Collectors.toList());

            ParkingLotMap result = ParkingLotMap.builder()
                .id(parkingLot.getId())
                .name(parkingLot.getName())
                .total(parkingLot.getTotal())
                .used(parkingLot.getUsed())
                .parkingInfoList(
                    parkingInfoDtoList
                )
                .build();

            return result;

        } catch (Exception e) {
            log.info("[ParkingService] {}", e.toString());

            throw e;
        }

    }


    @Override
    public ParkingInfoDto.Info findByParkingId(Long parkingInfoId) {

        try {
            /*parkingInfo 조회*/
            ParkingInfo parkingInfo = parkingInfoRepository.findById(parkingInfoId).orElseThrow(
                () -> new ParkingInfoException("ParkingInfo 오류")
            );

            /*Dto 생성 후 리턴*/
            if (parkingInfo.getParkingStatus() == ParkingStatus.USED) {
                return Info.builder()
                    .id(parkingInfo.getId())
                    .parkingId(parkingInfo.getParkingId())
                    .parkingStatus(parkingInfo.getParkingStatus())
                    .details(
                        CarDto.ParkingInfo.builder()
                            .id(parkingInfo.getMember().getCar().getId())
                            .carNumber(parkingInfo.getMember().getCar().getCarNumber())
                            .member(
                                UserParkingInfo.builder()
                                    .id(parkingInfo.getMember().getId())
                                    .userId(parkingInfo.getMember().getUserId())
                                    .userName(parkingInfo.getMember().getUserName())
                                    .build()
                            )
                            .time(parkingInfo.getLastModifiedTime())
                            .build()
                    )
                    .build();

            } else {
                return Info.builder()
                    .id(parkingInfo.getId())
                    .parkingId(parkingInfo.getParkingId())
                    .parkingStatus(parkingInfo.getParkingStatus())
                    .details(null)
                    .build();
            }


        } catch (ParkingInfoException e) {
            log.debug(e.getMessage());
            throw e;
        }
    }

    @Override
    public ParkingInfoDto.Info assign(Long parkingInfoId, String userId) {

        try {
            Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없음")
            );
            /*이미 배정 받은 상태면 안됨*/
            if (member.getParkingInfo() != null) {
                throw new Exception("해당 유저는 이미 자리를 배정받은 상태임.");
            }

            /*사용자 위약 정보 체크*/
            if (member.getMemberStatus() == MemberStatus.FORBIDDEN) {
                throw new Exception("해당 유저는 위약 상태입니다.");
            }

            ParkingInfo parkingInfo = parkingInfoRepository.findById(parkingInfoId).orElseThrow(
                () -> new ParkingInfoException("ParkingInfo Error")
            );

            /*해당 차량이 주차장 내에 들어와 있는지 확인해야됨*/
            String carNumber = member.getCar().getCarNumber();
            Car car = carRepository.findByCarNumber(carNumber).orElseThrow(
                () -> new Exception("일치하는 차량번호가 존재하지 않음.")
            );

            /*해제해 이거*/
            /*TODO*/
            if (!car.getCarEnterStatus().isEnter()) {
                throw new Exception("차량이 주차장 내에 존재하지 않음.");
            }

//            member.assignParking(parkingInfo);
            member.changeParkingStatus(parkingInfo);
            memberRepository.save(member);

            /*parkingLot used 증가*/
            ParkingLot parkingLot = parkingInfo.getParkingLot();
            parkingLot.increaseUsed();
            parkingLotRepository.save(parkingLot);

            log.info("[{}] {}번 자리 대여", this.getClass().getName(), parkingInfo.getParkingId());

            return
                ParkingInfoDto.Info
                    .builder()
                    .parkingId(parkingInfo.getParkingId())
                    .parkingStatus(ParkingStatus.USED)
//                .details(member.)
                    .build();

        } catch (UserNotFoundException e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());
            throw new RuntimeException(e);
        }

    }

    @Override
    public void parkingReturn(Long parkingInfoId, String userId) {

        /*TODO 리팩토링, 중복 로직 합치기, 기록 테이블 생성*/

        try {
            Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없음")
            );

            ParkingInfo parkingInfo = parkingInfoRepository.findById(parkingInfoId).orElseThrow(
                () -> new ParkingInfoException("ParkingInfo Error")
            );

            if (member.getParkingInfo() == null) {
                throw new Exception("자리 배정부터 수행하세요.");
            }

            member.changeParkingStatus(parkingInfo);
            memberRepository.save(member);

            /*parkingLot used 감소*/
            ParkingLot parkingLot = parkingInfo.getParkingLot();
            parkingLot.decreaseUsed();
            parkingLotRepository.save(parkingLot);


            /*반납할 때 출차 기록 및 관리자에게 알람*/
            String carNumber = member.getCar().getCarNumber();
            Optional<EntryLog> found = entryLogRepository.findFirstByCarNumberAndExitTimeIsNull(
                carNumber);



            /*TODO */
            if (found.isEmpty()) {
                throw new Exception("들어온 기록이 없음.");
            }

            EntryLog updated = found.get();
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            updated.setExitTime(zonedDateTime.toLocalDateTime());
            entryLogRepository.save(updated);
            eventPublisher.publishEvent(new EntryLogChangeEvent(updated.toDto()));
            log.info("[{}] 관리자에게 출차 알람", this.getClass().getName());

            // car is_entered 업데이트
            // Car 엔티티 @Setter 추가
            Optional<Car> found2 = carRepository.findByCarNumber(carNumber);
            if (found2.isEmpty()) {
                throw new Exception("error");
            }
            Car updated2 = found2.get();
            updated2.setCarEnterStatus(new CarEnterStatus(-1, false));
            carRepository.save(updated2);

            log.info("[{}] {}번 자리 반납", this.getClass().getName(), parkingInfo.getParkingId());
            log.info("[{}] {}번 출차 기록", this.getClass().getName(), parkingInfo.getParkingId());


        } catch (UserNotFoundException e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(), e.getMessage());
            throw new RuntimeException(e);
        }

    }


}