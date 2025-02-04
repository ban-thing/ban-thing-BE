package com.example.banthing.domain.item.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FlaskVectorizationResponseDto {
    private List<Long> vectorized_hashtags;

    // Constructors
    public FlaskVectorizationResponseDto(List<Long> vectorized_hashtags) {
        this.vectorized_hashtags = vectorized_hashtags;
    }   
}
