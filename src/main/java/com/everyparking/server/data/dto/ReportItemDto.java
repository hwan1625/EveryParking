package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.ReportStatus;
import com.everyparking.server.filestore.UploadFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportItemDto {
    private Long id;
    private String title;
    private String contents;
    private LocalDateTime createdTime;
    private ReportStatus reportStatus;
    private UserInfoForManager userInfo;
    private UploadFile files;
}

