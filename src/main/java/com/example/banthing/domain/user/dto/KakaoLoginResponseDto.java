package com.example.banthing.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoLoginResponseDto {

    private String jwt;
    private String message;

}
