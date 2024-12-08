package com.example.banthing.domain.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;

@Getter
public class FlaskRequestDto {
    private String input_hashtag;
    List<ItemResponseDto> items;

    // Constructors
    public FlaskRequestDto(List<ItemResponseDto> items) {
        this.items = items;
    }   

    public void setHashtag(String hashtags) {
        this.input_hashtag = hashtags;
    }
}
