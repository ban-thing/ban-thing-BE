package com.example.banthing.domain.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemReportRequestDto {
    private String hiReason;  // 상위 신고 사유
    private String loReason;  // 하위 신고 사유
}
