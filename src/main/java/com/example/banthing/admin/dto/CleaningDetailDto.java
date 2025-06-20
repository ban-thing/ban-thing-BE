package com.example.banthing.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CleaningDetailDto {
    private String pollution;
    private String timeUsed;
    private String purchasedDate;
    private String cleaned;
    private String expire;
}
