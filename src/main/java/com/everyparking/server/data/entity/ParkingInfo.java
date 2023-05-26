package com.everyparking.server.data.entity;

import com.everyparking.server.data.dto.ParkingDto.MyParkingStatus;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "ParkingInfo")
@Getter
@Builder
@AllArgsConstructor
@Embeddable
@Slf4j
public class ParkingInfo extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parkingInfo_id")
    private Long id;

    private int parkingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @Enumerated(value = EnumType.STRING)
    private ParkingStatus parkingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parkingLot_id")
    private ParkingLot parkingLot;


    @OneToOne(mappedBy = "parkingInfo")
    private Member member;


    public ParkingInfo() {

    }

    public MyParkingStatus toDto() {
        log.info("[{}]", this.getClass().getName());

        MyParkingStatus result = MyParkingStatus.builder()
            .parkingId(this.getParkingId())
            .remain(
//                calcTime()
                123
            )
            .carNumber(this.getCar().getCarNumber())
            .build();

        return result;
    }

    private int calcTime() {
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();

        // 두 날짜 사이의 시간 간격 계산
        Duration duration = Duration.between(this.getCreatedTime(), now);

        // 분으로 변환
        int minutes = (int) duration.toMinutes();

        log.info("[{}] remain : {}", this.getClass().getName(), minutes);

        return minutes;
    }


    /*parkingStatus 변경*/
    public void changeParkingStatus() {

//        이용가능한 경우 사용중으로 변환
        if (this.parkingStatus == ParkingStatus.AVAILABLE) {
            this.parkingStatus = ParkingStatus.USED;

//            사용중인 경우 이용가능으로 변환
        } else if (this.parkingStatus == ParkingStatus.USED) {
            this.parkingStatus = ParkingStatus.AVAILABLE;
        }
    }
}
