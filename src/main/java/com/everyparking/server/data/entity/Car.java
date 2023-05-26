package com.everyparking.server.data.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
//@Data
@Table(name = "Car")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Car extends BaseTime {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    private String carNumber;

    private String modelName;

    /**
     * 차량 등록 상태
     */
    @Enumerated(value = EnumType.STRING)
    private CarStatus carStatus;

    /**
     * 차량 출입상태
     */
//    private boolean isEnter = false;

    @Embedded
    private CarEnterStatus carEnterStatus;

    @OneToOne(mappedBy = "car")
    private Member member;

    @OneToMany(mappedBy = "car")
    private List<ParkingInfo> parkingInfo = new ArrayList<>();

    public Car() {

    }


    public void setEnter(int parkingLotId) {
        this.carEnterStatus.setEnter(parkingLotId);

    }
}
