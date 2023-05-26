package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.MemberStatus;
import com.everyparking.server.data.entity.RoleType;
import javax.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * Member dto
 * </p>
 */
public class MemberDto {

    /**
     * Member join Dto
     */
    @Builder
    @Data
    public static class UserFullInfo {

        private Long id;

        private int studentId;

        private String studentName;

        private String userId;

        private String password;

        private int phoneNumber;

        private String email;

        /**
         * Dto -> Entity
         *
         * @param joinDto
         * @return Member
         */
        public Member toEntity(UserFullInfo joinDto) {
            Member member = Member.builder()
                .id(joinDto.id)
                .userId(joinDto.userId)
                .password(joinDto.password)
                .userName(joinDto.studentName)
                .studentId(joinDto.studentId)
                .roleType(RoleType.USER)
                .userInfo(
                    com.everyparking.server.data.entity.UserInfo.builder()
                        .phoneNumber(joinDto.phoneNumber)
                        .email(joinDto.email)
                        .build())
                .memberStatus(MemberStatus.DEFAULT)
                .build();

            return member;
        }
    }

    /**
     * 자리배정 상세페이지의 사용자 조회를 위한 dto
     */
    @Data
    @Builder
    @MappedSuperclass
    public static class UserParkingInfo {


        private Long id;

        private String userId;

        private String userName;

    }


    /**
     * Member login Dto
     */

    public static class Login {

        @Builder
        @Data
        public static class Request {

            private String userId;

            private String password;
        }

        @Builder
        @Data
        public static class Response {

            /*TODO session으로 교체 예정*/
            private String userId;

            private boolean registered;


        }


    }

    @Builder
    @Data
    public static class UserInfoDto {

        private String studentName;

        private boolean status;

        public UserInfoDto(String studentName, boolean status) {
            this.studentName = studentName;
            this.status = status;
        }



    }

    public static MemberDto.UserInfoDto toDto(Member member) {
        return UserInfoDto.builder()
            .studentName(member.getUserName())
            .status(
                member.checkParkingStatus()
            )
            .build();

    }
    /*TODO 좋지 않은 방법*/

}
