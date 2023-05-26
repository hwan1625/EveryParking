package com.everyparking.server.data.entity;

import com.everyparking.server.data.dto.EntryLogDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "entryLog")
public class EntryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String carNumber;
    @Column(nullable = false)
    private LocalDateTime entryTime;
    @Column
    private LocalDateTime exitTime;

    public EntryLogDto toDto() {
        return EntryLogDto.builder()
                .id(getId())
                .carNumber(getCarNumber())
                .entryTime(getEntryTime())
                .exitTime(getExitTime())
                .build();
    }
}