package com.example.banthing.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateAddressRequestDto {

    private String address1;
    private String address2;
    private String address3;
}
