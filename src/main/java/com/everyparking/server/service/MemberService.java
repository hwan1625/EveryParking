package com.everyparking.server.service;

import com.everyparking.server.data.dto.MemberDto;
import com.everyparking.server.data.dto.MemberDto.UserFullInfo;
import com.everyparking.server.data.entity.Member;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {


    void join(UserFullInfo joinDto);

    Member login(MemberDto.Login.Request loginDto);

    /*userId로 회원 조회 - 메인화면 - userInfo*/
    MemberDto.UserInfoDto findByUserId(String userId);

    List<UserFullInfo> findAll();
}
