package com.example.banthing.domain.item.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Item;

@Getter
public class ItemListResponseDto {
    
    private List<ItemSearchResponseDto> items;

    // Constructors
    public ItemListResponseDto(List<ItemSearchResponseDto> items) {
        this.items = items;
    }   

}