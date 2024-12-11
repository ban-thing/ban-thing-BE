package com.example.banthing.domain.item.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class FlaskRequestDto {
    private String input_hashtag;
    List<ItemSearchResponseDto> items;

    // Constructors
    public FlaskRequestDto(List<ItemSearchResponseDto> items) {
        this.items = items;
    }   

    public void setHashtag(String hashtags) {
        this.input_hashtag = hashtags;
    }
}
