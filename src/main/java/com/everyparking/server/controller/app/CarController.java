package com.everyparking.server.controller.app;

import com.everyparking.server.data.dto.CarDto;
import com.everyparking.server.exception.CarValidationException;
import com.everyparking.server.service.CarService;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/car")
@AllArgsConstructor
@Slf4j
public class CarController {

    private final CarService carService;


    @PostMapping("/register")
    public ResponseEntity<?> registerCar(HttpServletRequest request,
        @RequestBody CarDto.Register register) {

        String userId = request.getHeader("userId");

        try {

            return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    carService.register(register, userId));

            /**/
        } catch (CarValidationException e) {
            log.info("[CarController] {}", e.toString());

            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();

        } catch (Exception e) {
            log.info("[CarController] {}", e.toString());

            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
        }
    }

}
