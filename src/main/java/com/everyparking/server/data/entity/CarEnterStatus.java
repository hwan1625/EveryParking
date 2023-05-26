package com.everyparking.server.data.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.ToString;

@Embeddable
@ToString
@Getter
public class CarEnterStatus {

    private int parkingLotId;

    private boolean isEnter;

    public CarEnterStatus() {

    }

    public CarEnterStatus(int parkingLotId, boolean isEnter) {
        this.parkingLotId = parkingLotId;
        this.isEnter = isEnter;
    }

    public void setEnter(int parkingLot) {
        this.parkingLotId = parkingLot;
        this.isEnter = true;

    }
}
