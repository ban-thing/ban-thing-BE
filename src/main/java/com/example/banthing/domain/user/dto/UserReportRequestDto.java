package com.example.banthing.domain.user.dto;


import lombok.Getter;

@Getter
public class UserReportRequestDto {
    private String reason;
    private String detailed_reason;
}