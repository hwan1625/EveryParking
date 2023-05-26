package com.everyparking.server.data.dto;

import com.everyparking.server.data.entity.ParkingStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * ParkingDto
 */
@Slf4j
public class ParkingDto {

    /**
     * 메인화면 - myParkingStatus
     */
    @Data
    @Builder
    @ToString
    public static class MyParkingStatus {

        private int parkingId;
        private int remain;
        private String carNumber;

        public MyParkingStatus(int parkingId, int remain, String carNumber) {
            this.parkingId = parkingId;
            this.remain = remain;
            this.carNumber = carNumber;
        }


    }

    @Data
    @Builder
    public static class ParkingLotMap {

        private Long id;
        private String name;

        private int total = 0;

        private int used = 0;

        private List<ParkingInfoDto.Map> parkingInfoList = new ArrayList<>();

    }

    public static class ParkingInfoDto {


        @Data
        @Builder
        public static class Map {

            private Long id;

            private int parkingId;

            private ParkingStatus parkingStatus;

        }


        /**
         * 자리배정 상세페이지의 사용자 조회를 위한 dto
         */
        @Data
        @Builder
        public static class Info {

            private Long id;

            private int parkingId;

            private ParkingStatus parkingStatus;

//            private CarDto.ParkingInfo car;

            private Object details;


        }


    }


}
