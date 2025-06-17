package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;
import com.example.banthing.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemReportDetailResponseDto {
    ItemReport report;
    Item item;
    CleaningDetail cleaningDetail;
}
