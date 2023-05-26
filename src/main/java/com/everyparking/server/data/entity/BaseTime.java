package com.everyparking.server.data.entity;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTime {

    @CreatedDate
    /*TODO DateFormat 설정*/
    private LocalDateTime createdTime;

    @LastModifiedBy
    private LocalDateTime lastModifiedTime;


}
