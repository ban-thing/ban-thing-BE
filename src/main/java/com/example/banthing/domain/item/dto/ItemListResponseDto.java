package com.example.banthing.domain.item.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Item;

@Getter
public class ItemListResponseDto {
    
    private int totalPages;
    private List<ItemResponseDto> items;

    // Constructors
    public ItemListResponseDto(List<ItemResponseDto> items) {
        this.items = items;
    }   

}