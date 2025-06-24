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
    private String reason;
    private LocalDateTime createdAt;
    private Long reporterId;
    private Long reportedUserId;
    private String status;

    private LocalDateTime createdAt_item;
    private String nickname_item;
    private String address_item;
    private String content_item;
    
    private String pollution;
    private String timeUsed;
    private String purchasedDate;
    private String cleaned;

    private String itemContent;
    private LocalDateTime itemCreatedAt;
    private String sellerNickname;
    private List<String> hashtags;
    private CleaningDetailDto cleaningDetail;
}

