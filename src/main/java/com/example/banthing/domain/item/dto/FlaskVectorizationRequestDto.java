package com.example.banthing.domain.item.dto;

import java.util.List;

public class FlaskVectorizationRequestDto {
    private List<String> input_hashtag;

    // Constructors
    public FlaskVectorizationRequestDto(List<String> hashtags) {
        this.input_hashtag = hashtags;
    }   
}
