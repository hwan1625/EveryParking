package com.everyparking.server.service;

import com.everyparking.server.data.dto.EntryLogDto;
import com.everyparking.server.data.dto.MemberDto;
import com.everyparking.server.data.dto.ReportItemDto;
import com.everyparking.server.data.dto.UserInfoForManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ManagerWebService {
    Page<EntryLogDto> getAllEntryLogs(Pageable pageable);
    List<ReportItemDto> getAllReportLogs();
    void violation(String userId);
    void reportCancel(Long reportId);
    UserInfoForManager searchUser(String value, int option) throws Exception;
    void userViolation(String userId);
    void userUnViolation(String userId);
}