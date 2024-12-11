package com.example.banthing.domain.item.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class ItemSearchRequestDto {

    private final String keyword;
    private final String hashtags;
    private final Long minPrice;
    private final Long maxPrice;
    private final String address;

    public ItemSearchRequestDto(String keyword, List<String> hashtags, Long minPrice, Long maxPrice, String address){

        this.keyword = keyword;
        this.hashtags = String.join(", ", hashtags);
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.address = address;

    }

}

