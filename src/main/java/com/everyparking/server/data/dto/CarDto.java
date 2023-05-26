package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.Car;
import com.everyparking.server.data.entity.CarEnterStatus;
import com.everyparking.server.data.entity.CarStatus;
import com.everyparking.server.data.entity.Member;
import lombok.Builder;
import lombok.Data;

public class CarDto {

    @Data
    @Builder
    public static class Register {

//        private String userId;

        private String carNumber;

        private String modelName;

        public Car toEntity(CarDto.Register register, Member member) {
            return
                Car.builder()
                    .carNumber(register.carNumber)
                    .modelName(register.modelName)
                    .carStatus(CarStatus.WAITING)
                    .member(member)
                    .carEnterStatus(new CarEnterStatus(-1, false))
                    .build();
        }

    }

    /**
     * 자리배정 상세페이지의 사용자 조회를 위한 dto
     */
    @Builder
    @Data
    public static class ParkingInfo {

        private Long id;

        private String carNumber;

        private MemberDto.UserParkingInfo member;
    }



}
