package com.everyparking.server.data.dto;


import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 주차 차단기 dto
 * </p>
 */
public class ParkingBreakerDto {

    @Data
    public static class Request {

        private String plateNumber;

        private int parkingLotId;

        public Request() {

        }

    }

    @Data
    @Builder
    public static class Response {

        private boolean valid = false;

    }


}
