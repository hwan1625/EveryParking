package com.everyparking.server.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerParkingInfoDto {
    private ParkingDto.ParkingLotMap parkingLotMap;
    private List<ParkingDto.ParkingInfoDto.Info> assignInfo;
}
