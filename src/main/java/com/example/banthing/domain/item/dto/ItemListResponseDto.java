package com.example.banthing.domain.item.dto;

import lombok.Getter;
import java.util.List;

@Getter
public class ItemListResponseDto {
    
    private List<ItemSearchResponseDto> items;

    // Constructors
    public ItemListResponseDto(List<ItemSearchResponseDto> items) {
        this.items = items;
    }   

}