package com.example.banthing.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemReportRequestDto {
    private String reason;  // 신고 사유
}
