package com.everyparking.server.data.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class SearchDto {
    private String value;
    private int option;
}
