package com.example.banthing.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Item;

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

    private Item item;

    private String itemContent;
    private LocalDateTime itemCreatedAt;
    private String sellerNickname;
    private List<String> hashtags;
    private CleaningDetailDto cleaningDetail;
}

