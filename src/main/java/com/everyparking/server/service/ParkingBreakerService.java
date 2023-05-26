package com.everyparking.server.service;

import com.everyparking.server.data.dto.ParkingBreakerDto;
import org.springframework.stereotype.Service;

@Service
public interface ParkingBreakerService {

    /**
     * <p>
     *     차량 번호를 검색해서 일치하는 사용자가 있는지 검증
     * </p>
     * <p>
     *     일치하는 경우 Car의 isEnter = true로 설정 후
     * </p>
     * <p>
     *     Car의 CarEnterStatus에 (parkingLotId, true)를 설정
     * </p>
     *
     * @param request
     * @return ParkingBreakerDto.Response
     *
     */
    ParkingBreakerDto.Response isValid(ParkingBreakerDto.Request request);

    void entry(String fileFullName);

    void exit(String carNumber);

}
