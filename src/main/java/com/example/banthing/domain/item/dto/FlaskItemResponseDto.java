package com.example.banthing.domain.item.dto;

import java.time.LocalDateTime;

import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ItemType;

import lombok.Getter;

@Getter
public class FlaskItemResponseDto {
    private final Long id;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String address;
    private final Integer price;
    private final String title;
    private final ItemType type;
    private final String hashtag;
    private final String images;
    private final ItemStatus status;

    public FlaskItemResponseDto(
        Long id, 
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String address,
        Integer price,
        String title,
        String type,    
        String hashtag,
        String images,
        ItemStatus status
    ) {
            this.id = id;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.address = address;
            this.price = price;
            this.title = title;
            this.type = ItemType.valueOf(type);
            this.hashtag = hashtag;
            this.images = images;
            this.status = status;
    }
}