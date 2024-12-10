package com.example.banthing.domain.item.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class ItemSearchRequestDto {

    private final String keyword;
    private final String hashtags;
    private final int filter_low;
    private final int filter_high;
    private final String address;

    public ItemSearchRequestDto(String keyword, List<String> hashtags, int filter_low, int filter_high, String address){

        this.keyword = keyword;
        this.hashtags = String.join(", ", hashtags);
        this.filter_low = filter_low;
        this.filter_high = filter_high;
        this.address = address;

    }

}

