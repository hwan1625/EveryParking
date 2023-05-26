package com.everyparking.server.data.repository;

import com.everyparking.server.data.entity.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {

    Optional<Car> findByCarNumber(String carNumber);


}
