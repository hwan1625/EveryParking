package com.everyparking.server.data.entity;

import javax.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;

@Embeddable

@Data
@Builder
public class UserInfo {

    private int phoneNumber;
//    private String address;
    private String email;


    public UserInfo(int phoneNumber, String email) {
        this.phoneNumber = phoneNumber;
//        this.address = address;
        this.email = email;
    }

    public UserInfo() {

    }

    //    @Nullable
//    private int studentId;

}
