package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.*;
import com.everyparking.server.data.entity.*;
import com.everyparking.server.data.repository.*;
import com.everyparking.server.event.EntryLogChangeEvent;
import com.everyparking.server.filestore.UploadFile;
import com.everyparking.server.service.ManagerWebService;
import com.everyparking.server.service.ParkingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ManagerWebServiceImpl implements ManagerWebService {
    private final EntryLogRepository entryLogRepository;
    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;
    private final CarRepository carRepository;
    private final ParkingService parkingService;
    private final ParkingInfoRepository parkingInfoRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final UploadFileRepository uploadFileRepository;

    @Override
    public Page<EntryLogDto> getAllEntryLogs(Pageable pageable) {
        Page<EntryLogDto> entryLogs = entryLogRepository.findAllByOrderByEntryTimeDesc(pageable).map(EntryLog::toDto);
        return entryLogs;
    }

    public List<ReportItemDto> getAllReportLogs() {
        /*신고 list 조회*/
        List<Report> reports = reportRepository.findAllByOrderByCreatedTimeDesc();
        List<ReportItemDto> reportItemDto = new ArrayList<>();
        reports.forEach(item -> {
            /*reportId로 이미지 찾기, 이미지 없어도 됨.*/
            UploadFile file = uploadFileRepository.findByReportId(item.getId());

            /*userId로 회원 찾기, 작성자가 존재해야함.*/
            try {
                Member member = memberRepository.findByUserId(item.getUserId()).orElseThrow(
                        () -> new Exception("작성자가 존재하지 않음.")
                );

                reportItemDto.add(ReportItemDto.builder()
                        .id(item.getId())
                        .createdTime(item.getCreatedTime())
                        .title(item.getTitle())
                        .contents(item.getContents())
                        .reportStatus(item.getReportStatus())
                        .userInfo(UserInfoForManager.builder()
                                .userId(member.getUserId())
                                .userName(member.getUserName())
                                .studentId(member.getStudentId())
                                .email(member.getUserInfo().getEmail())
                                .phoneNumber(member.getUserInfo().getPhoneNumber())
                                .build())
                        .files(file)
                        .build());

            } catch (Exception e) {
                log.debug(e.getMessage());
                throw new RuntimeException(e);
            }
        });
        return reportItemDto;
    }

    @Override
    public void violation(String userId) {
        try {
            Optional<Member> found = memberRepository.findByUserId(userId);
            if (found.isEmpty()) {
                throw new Exception("회원이 존재하지 않습니다.");
            }
            /*회원 상태 정보 갱신 -> FORBIDDEN*/
            Member updated = found.get();
            updated.setMemberStatus(MemberStatus.FORBIDDEN);
            memberRepository.save(updated);

            // car is_entered 업데이트
            // Car 엔티티 @Setter 추가
            Optional<Car> found3 = carRepository.findById(found.get().getCar().getId());
            if (found3.isEmpty()) {
                throw new Exception("error");
            }
            Car updated3 = found3.get();
            updated3.setCarEnterStatus(new CarEnterStatus(-1, false));
            carRepository.save(updated3);

            Optional<EntryLog> found2 = entryLogRepository.findFirstByCarNumberAndExitTimeIsNull(found3.get().getCarNumber());
            if (found2.isEmpty()) {
                throw new Exception("들어온 기록이 없음.");
            }

            EntryLog updated2 = found2.get();
            ZoneId zoneId = ZoneId.of("Asia/Seoul");
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            updated2.setExitTime(zonedDateTime.toLocalDateTime());
            entryLogRepository.save(updated2);
            /*관리자 페이지 출차 기록*/
            eventPublisher.publishEvent(new EntryLogChangeEvent(updated2.toDto()));

            /*member parking info update*/
            Optional<Member> member = memberRepository.findByCarId(updated3.getId());
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

        } catch (Exception e) {

        }
    }

    @Override
    public void reportCancel(Long reportId) {
        try {
            Report report = reportRepository.findById(reportId).orElseThrow(
                    () -> new Exception("존재하지 않는 신고 목록입니다.")
            );
            report.changeStatus();
            reportRepository.save(report);

            eventPublisher.publishEvent(new EntryLogChangeEvent(
                    ReportItemDto.builder()
                            .id(report.getId())
                            .reportStatus(report.getReportStatus())
                            .build()
            ));
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    @Override
    public UserInfoForManager searchUser(String value, int option) throws Exception {
        if (option == 0) {
            // 유저 아이디로 검색
            Member member = memberRepository.findByUserId(value).orElseThrow(
                    () -> new Exception("작성자가 존재하지 않음.")
            );
            // 차량은 없어도 됨.
            if (member.getCar() == null) {
                return UserInfoForManager.builder()
                        .userId(member.getUserId())
                        .userName(member.getUserName())
                        .email(member.getUserInfo().getEmail())
                        .phoneNumber(member.getUserInfo().getPhoneNumber())
                        .studentId(member.getStudentId())
                        .memberStatus(member.getMemberStatus())
                        .build();
            } else {
                Car car = carRepository.findById(member.getCar().getId()).get();
                return UserInfoForManager.builder()
                        .userId(member.getUserId())
                        .userName(member.getUserName())
                        .email(member.getUserInfo().getEmail())
                        .phoneNumber(member.getUserInfo().getPhoneNumber())
                        .studentId(member.getStudentId())
                        .carNumber(car.getCarNumber())
                        .modelName(car.getModelName())
                        .memberStatus(member.getMemberStatus())
                        .build();
            }
        } else if (option == 1) {
            // 차량 번호로 검색
            Car car = carRepository.findByCarNumber(value).orElseThrow(
                    () -> new Exception("차량번호가 존재하지 않음.")
            );
            Member member = memberRepository.findByCarId(car.getId()).orElseThrow(
                    () -> new Exception("존재하지 않는 회원.")
            );
            // 유저가 있어야됨.
            return UserInfoForManager.builder()
                    .userId(member.getUserId())
                    .userName(member.getUserName())
                    .email(member.getUserInfo().getEmail())
                    .phoneNumber(member.getUserInfo().getPhoneNumber())
                    .studentId(member.getStudentId())
                    .carNumber(car.getCarNumber())
                    .modelName(car.getModelName())
                    .memberStatus(member.getMemberStatus())
                    .build();
        }
        return null;
    }

    @Override
    public void userViolation(String userId) {
        try {
            Member member = memberRepository.findByUserId(userId).orElseThrow(
                    () -> new Exception("존재하지 않는 회원.")
            );
            if (member.getMemberStatus() == MemberStatus.DEFAULT) {
                member.setMemberStatus(MemberStatus.FORBIDDEN);
                memberRepository.save(member);

                eventPublisher.publishEvent(new EntryLogChangeEvent(
                        UserViolationStatusDto.builder()
                                .userId(userId)
                                .memberStatus(MemberStatus.FORBIDDEN)
                                .build()));
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    @Override
    public void userUnViolation(String userId) {
        try {
            Member member = memberRepository.findByUserId(userId).orElseThrow(
                    () -> new Exception("존재하지 않는 회원.")
            );
            if (member.getMemberStatus() == MemberStatus.FORBIDDEN) {
                member.setMemberStatus(MemberStatus.DEFAULT);
                memberRepository.save(member);

                eventPublisher.publishEvent(new EntryLogChangeEvent(
                        UserViolationStatusDto.builder()
                            .userId(userId)
                            .memberStatus(MemberStatus.DEFAULT)
                            .build()));
            }
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }
}
