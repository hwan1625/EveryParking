package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.ParkingLot;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {


}
