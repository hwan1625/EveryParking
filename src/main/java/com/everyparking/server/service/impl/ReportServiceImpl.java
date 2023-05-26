package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.ReportDto;
import com.everyparking.server.data.dto.ReportItemDto;
import com.everyparking.server.data.dto.UserInfoForManager;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.Report;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.data.repository.ReportRepository;
import com.everyparking.server.data.repository.UploadFileRepository;
import com.everyparking.server.event.EntryLogChangeEvent;
import com.everyparking.server.exception.UserNotFoundException;
import com.everyparking.server.filestore.FileStore;
import com.everyparking.server.filestore.UploadFile;
import com.everyparking.server.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;
    private final UploadFileRepository uploadFileRepository;

    private final FileStore fileStore;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void uploadReport(String userId, ReportDto reportDto, MultipartFile multipartFile) {

        try {

            Member member = memberRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("사용자 없음"));

            Report report = reportDto.toEntity(member);

            Report savedReport = reportRepository.save(report);

            UploadFile uploadFile = fileStore.storeFile(multipartFile, userId);
            uploadFile.setReport(report);

            uploadFileRepository.save(uploadFile);

//             실시간 관리자 알람
            eventPublisher.publishEvent(new EntryLogChangeEvent(
                ReportItemDto.builder()
                        .id(savedReport.getId())
                        .createdTime(savedReport.getCreatedTime())
                        .title(savedReport.getTitle())
                        .contents(savedReport.getContents())
                        .reportStatus(savedReport.getReportStatus())
                        .userInfo(UserInfoForManager.builder()
                                .userId(member.getUserId())
                                .userName(member.getUserName())
                                .build())
                        .files(uploadFile)
                        .build()
            ));

        } catch (Exception e) {
            log.info("[{}] {}", this.getClass().getName(),
                e.getMessage());

        }


    }


}

