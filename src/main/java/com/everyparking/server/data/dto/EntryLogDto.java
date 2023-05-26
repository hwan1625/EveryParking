package com.everyparking.server.data.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 관리자 웹페이지
 * 웹 소켓 전용 단일 DTO
 */
@Data
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class EntryLogDto {
    private Long id;
    private String carNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

}
