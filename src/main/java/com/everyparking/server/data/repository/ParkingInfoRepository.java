package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.Car;
import com.everyparking.server.data.entity.Member;
import com.everyparking.server.data.entity.ParkingInfo;
import com.everyparking.server.data.entity.ParkingLot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingInfoRepository extends JpaRepository<ParkingInfo, Long> {

    Optional<ParkingInfo> findByMember_Id(String userId);

    Optional<ParkingInfo> findByCar(Car car);

    Optional<ParkingInfo> findByParkingLot(ParkingLot parkingLot);

    Optional<List<ParkingInfo>> findAllByParkingLot(ParkingLot parkingLot);

    Optional<ParkingInfo> findById(Long parkingInfoId);
    Optional<ParkingInfo> findByParkingId(Long parkingInfoId);

    Optional<ParkingInfo> findByMember_UserId(String userId);


}
