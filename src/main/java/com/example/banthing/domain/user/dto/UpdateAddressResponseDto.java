package com.example.banthing.domain.user.dto;

import com.example.banthing.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UpdateAddressResponseDto {
    private final String beforeAddress1;
    private final String beforeAddress2;
    private final String beforeAddress3;
    private String afterAddress1;
    private String afterAddress2;
    private String afterAddress3;

    public UpdateAddressResponseDto(User user) {
        this.beforeAddress1 = user.getAddress1();
        this.beforeAddress2 = user.getAddress2();
        this.beforeAddress3 = user.getAddress3();
    }

    public void updateAddress(User user) {
        this.afterAddress1 = user.getAddress1();
        this.afterAddress2 = user.getAddress2();
        this.afterAddress3 = user.getAddress3();
    }
}
