package com.example.banthing.domain.item.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlaskVectorizationRequestDto {
    @JsonProperty("input_hashtag")
    private String input_hashtag;

    // Constructors
    public FlaskVectorizationRequestDto(String hashtags) {
        this.input_hashtag = hashtags;
    }   
}
