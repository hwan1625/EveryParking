package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.MemberDto;
import com.everyparking.server.data.dto.MemberDto.UserFullInfo;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.RoleType;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.exception.DuplicateUserException;
import com.everyparking.server.exception.InvalidPwdException;
import com.everyparking.server.exception.UserNotFoundException;
import com.everyparking.server.service.MemberService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
@Service
//@Transactional(readOnly = true)
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     *
     * @param joinDto
     */
    @Override
    public void join(UserFullInfo joinDto) {

        Member member = joinDto.toEntity(joinDto);

        try {
            memberRepository.findByUserId(joinDto.getUserId())
                .orElseThrow(() ->
                    new UserNotFoundException("가입 가능"));
            throw new DuplicateUserException("중복된 아이디");


        } catch (UserNotFoundException e) {
            try {
                memberRepository.save(member);

                log.info("[MemberService] {} 가입 성공", joinDto.toString());


            } catch (Exception ex) {
                log.info(ex.toString());
                throw new RuntimeException("회원가입 실패");
            }

//            log.info(member.toString());

        }


    }

    /**
     * 로그인
     *
     * @param loginDto
     */
    @Override
    public Member login(MemberDto.Login.Request loginDto) {
//        Member findMember = memberRepository.findByUserId(loginDto.getUserId()).orElseGet(null);
        Member findMember = memberRepository.findByUserId(loginDto.getUserId())
            .orElseThrow(
                () -> new UserNotFoundException("존재하지 않는 회원"));
        log.info("[MemberService] {}", findMember.toString());
        if (findMember != null) {
            if (findMember.getPassword().equals(loginDto.getPassword())) {
                log.info("[MemberService] 로그인 성공");
                return findMember;
            } else {
//                log.info("[MemberService] 비밀번호 오류");
                throw new InvalidPwdException("비밀번호 오류");
            }
        } else {

            return null;
        }

    }


    /**
     * userId로 회원 조회 메인화면 - userInfo
     */
    @Override
    public MemberDto.UserInfoDto findByUserId(String userId) {
        try {
            Member findMember = memberRepository.findByUserId(userId)
                .orElseThrow(
                    () -> new UserNotFoundException("사용자 정보를 찾을 수 없습니다."));

//            return MemberDto.toDto(findMember);
            return MemberDto.toDto(findMember);

        } catch (Exception e) {
            log.info("[MemberService] {}", e.toString());
        }

        throw new IllegalStateException("userInfo error");
    }

    /**
     * 회원 목록 조회
     */
    @Override
    public List<UserFullInfo> findAll() {

        try {
            List<Member> findAll = memberRepository.findAllByRoleType(RoleType.USER).orElseThrow(
                () -> new UserNotFoundException("회원 목록 조회 오류")
            );

            List<UserFullInfo> result = findAll.stream()
                .map(
                    o -> UserFullInfo
                        .builder()
                        .id(o.getId())
                        .studentId(o.getStudentId())
                        .studentName(o.getUserName())
                        .userId(o.getUserId())
                        .password(null)
                        .phoneNumber(o.getUserInfo().getPhoneNumber())
                        .email(o.getUserInfo().getEmail())
                        .build()
                )
                .collect(Collectors.toList());

            return result;

        } catch (Exception e) {
            log.info("[MemberService] {}", e.toString());
            throw e;
        }


    }
}
