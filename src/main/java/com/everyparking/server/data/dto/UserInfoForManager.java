package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.MemberStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoForManager {
    private String userId;
    private String userName;
    private int studentId;
    private String email;
    private int phoneNumber;
    private String carNumber;
    private String modelName;
    private MemberStatus memberStatus;
}
