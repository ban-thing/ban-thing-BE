package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Item;
import lombok.Getter;

@Getter
public class ItemResponseDto {
    private final Long itemId;

    public ItemResponseDto(Item item) {
        this.itemId = item.getId();
    }
}
