package com.example.banthing.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminUserResponseDto {
    private Long userId;
    private String nickname;
    private String status;
    private int reportCount;
    private LocalDateTime createdAt;

    @QueryProjection
    public AdminUserResponseDto(Long userId, String nickname, String status, int reportCount, LocalDateTime createdAt) {
        this.userId = userId;
        this.nickname = nickname;
        this.status = status;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
    }
}
