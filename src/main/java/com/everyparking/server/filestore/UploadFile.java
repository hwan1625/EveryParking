package com.everyparking.server.filestore;

import com.everyparking.server.data.entity.BaseTime;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.Report;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "UploadFile")
@Getter
@Setter
@AllArgsConstructor
public class UploadFile extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String uploadFileName;

    private String storeFileName;

    @OneToOne(fetch = FetchType.LAZY)
    private Report report;

    public UploadFile(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
//        this.report = report;

    }

    public void setReport(Report report) {
        this.report = report;

    }


    public UploadFile() {

    }
}
