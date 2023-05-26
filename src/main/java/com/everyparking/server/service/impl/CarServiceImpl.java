package com.everyparking.server.service.impl;

import com.everyparking.server.data.dto.CarDto;
import com.everyparking.server.data.entity.Car;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.repository.CarRepository;
import com.everyparking.server.data.repository.MemberRepository;
import com.everyparking.server.exception.CarException;
import com.everyparking.server.exception.CarValidationException;
import com.everyparking.server.exception.UserNotFoundException;
import com.everyparking.server.service.CarService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final MemberRepository memberRepository;


    @Override
    public CarDto.Register register(CarDto.Register register, String userId) {

        try {
            /*사용자 조회*/
            Member findMember = memberRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException("사용자를 찾을 수 없음")
            );

            try {
                /*차량 번호 중복 조회*/
                carRepository.findByCarNumber(register.getCarNumber()).orElseThrow(
                    () -> new CarException("등록 가능 차량")
                );

                throw new CarValidationException("이미 등록된 차량");

            } catch (CarException e) {
                /*차량 저장*/
                Car car = register.toEntity(register, findMember);
                findMember.registerCar(car);

                log.info("[CarService] {}", car.toString());
                carRepository.save(car);

                log.info("[CarService] {} 차량 등록 완료", userId);

                return register;


            } catch (Exception e) {
                throw e;
            }

        } catch (UserNotFoundException e) {
            log.info("[CarService] {}", e.toString());
            throw e;

        } catch (Exception e) {
            log.info("[CarService] {}", e.toString());
            throw e;
        }

    }
}
