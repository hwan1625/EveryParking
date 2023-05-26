package com.everyparking.server.service;

import com.everyparking.server.data.dto.CarDto;
import org.springframework.stereotype.Service;

@Service
public interface CarService {

    /**
     * <p>
     * 차량 등록 로직
     * </p>
     * <p>
     * CarDto.Register과 userId를 넘겨받아서 사용자를 조회한 후 차량을 등록하는 로직
     * </p>
     * <p>
     * 차량 등록 성공 시 CarDto.Register를 리턴하고 실패할 경우 Exception을 발생시킴
     * </p>
     *
     * @param register
     * @param userId
     * @return CarRegister.Register register
     */
    CarDto.Register register(CarDto.Register register, String userId);
}
