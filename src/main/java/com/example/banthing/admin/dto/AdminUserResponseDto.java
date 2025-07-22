package com.example.banthing.admin.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class AdminUserResponseDto {
    private Long userId;
    private String nickname;
    private String status;
    private int reportCount;
    private LocalDateTime createdAt;
    private List<ReportDetail> reportDetails;  // 신고 이력 추가

    @QueryProjection
    public AdminUserResponseDto(Long userId, String nickname, String status, int reportCount, LocalDateTime createdAt, List<ReportDetail> reportDetails) {
        this.userId = userId;
        this.nickname = nickname;
        this.status = status;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
        this.reportDetails = reportDetails;
    }

    public AdminUserResponseDto(Long userId, String nickname, String status, int reportCount, LocalDateTime createdAt) {
        this(userId, nickname, status, reportCount, createdAt, new ArrayList<>());
    }

    @Getter
    @AllArgsConstructor
    public static class ReportDetail {
        private Long reportId;
        private LocalDateTime createdAt;
        private String reason;
        private String reporterNickname;  // 추가
        private Long reporterId;
    }

}