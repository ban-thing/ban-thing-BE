package com.example.banthing.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdminUserDeletionResponseDto {
    private Long userId;
    private LocalDateTime joinedAt;
    private LocalDateTime deletedAt;
    private LocalDateTime lastLoginAt;
    private String reason;
    private String memo;
    private boolean isRejoinRestricted;
}

