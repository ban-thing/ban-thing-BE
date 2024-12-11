package com.example.banthing.domain.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.entity.ItemType;

import lombok.Getter;

@Getter
public class FlaskItemResponseDto {
    private final Long id;
    private final LocalDateTime updated_at;
    private final String address;
    private final Integer price;
    private final String title;
    private final ItemType type;
    private final String hashtag;
    private final String images;

    public FlaskItemResponseDto(
        Long id, 
        LocalDateTime updated_at,
        String address,
        Integer price,
        String title,
        String type,    
        String hashtag,
        String images
    ) {
            this.id = id;
            this.updated_at = updated_at;
            this.address = address;
            this.price = price;
            this.title = title;
            this.type = ItemType.valueOf(type);
            this.hashtag = hashtag;
            this.images = images;
    }
}