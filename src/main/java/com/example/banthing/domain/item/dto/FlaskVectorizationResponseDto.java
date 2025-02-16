package com.example.banthing.domain.item.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class FlaskVectorizationResponseDto {
    private String vectorized_hashtags1;
    private String vectorized_hashtags2;

    // Constructors
    public FlaskVectorizationResponseDto(String vectorized_hashtags1, String vectorized_hashtags2) {
        this.vectorized_hashtags1 = vectorized_hashtags1;
        this.vectorized_hashtags2 = vectorized_hashtags2;
    }   
}
