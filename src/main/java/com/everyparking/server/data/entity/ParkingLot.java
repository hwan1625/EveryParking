package com.everyparking.server.data.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "ParkingLot")
@Builder
@AllArgsConstructor
@Getter
public class ParkingLot extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parkingLot_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parkingLot")
    private List<ParkingInfo> parkingInfoList = new ArrayList<>();

    private int total = 0;

    private int available = 0;


    private int used = 0;

    public ParkingLot() {
    }

}
