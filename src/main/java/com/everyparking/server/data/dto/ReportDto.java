package com.everyparking.server.data.dto;


import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.Report;
import com.everyparking.server.data.entity.ReportStatus;
import com.everyparking.server.filestore.UploadFile;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportDto {

    private Long id;

    private String title;

    private String contents;

    private String userId;

    public Report toEntity(Member member) {
        return Report.builder()
            .title(this.title)
            .contents(this.contents)
            .userId(member.getUserId())
            .reportStatus(ReportStatus.WAITING)
            .build();

    }







}
