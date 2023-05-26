package com.everyparking.server.data.entity;

import com.everyparking.server.data.dto.ReportDto;
import com.everyparking.server.filestore.UploadFile;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.util.Lazy;

@Entity
@Builder
@Getter
@AllArgsConstructor
public class Report extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    private String title;

    private String contents;
    @Enumerated(EnumType.STRING)
    private ReportStatus reportStatus;

    @OneToOne(cascade = CascadeType.PERSIST )
    @JoinColumn(name = "image_id")
    private UploadFile uploadFiles;

    private String userId;

    public Report() {

    }

    public void changeStatus() {
        if (this.reportStatus == ReportStatus.WAITING) {
            this.reportStatus = ReportStatus.COMPLETE;
        }
    }

    public ReportDto toDto() {
        return ReportDto.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .userId(this.userId)
                .build();
    }
}
