package com.example.banthing.domain.item.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FlaskVectorizationResponseDto {
    private List<Long> vectorized_hashtags1;
    private List<Long> vectorized_hashtags2;

    // Constructors
    public FlaskVectorizationResponseDto(List<Long> vectorized_hashtags1, List<Long> vectorized_hashtags2) {
        this.vectorized_hashtags1 = vectorized_hashtags1;
        this.vectorized_hashtags2 = vectorized_hashtags2;
    }   
}
