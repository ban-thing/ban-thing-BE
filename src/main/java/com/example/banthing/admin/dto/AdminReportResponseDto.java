package com.example.banthing.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Item;

import jakarta.persistence.Column;

@Getter
@AllArgsConstructor
public class AdminReportResponseDto {
    private Long reportId;
    private String itemTitle;
    private String hiReason;
    private String loReason;
    private LocalDateTime createdAt;
    private Long reporterId;
    private Long reportedUserId;
    private String status;

    private String itemContent;
    private LocalDateTime itemCreatedAt;
    private String sellerNickname;
    private List<String> hashtags;

    private String pollution;
    private String timeUsed;
    private String purchasedDate;
    private String cleaned;

}

