package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserViolationStatusDto {
    private String userId;
    private MemberStatus memberStatus;
}
