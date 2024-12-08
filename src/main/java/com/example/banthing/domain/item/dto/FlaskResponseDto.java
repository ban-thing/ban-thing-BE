package com.example.banthing.domain.item.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.banthing.domain.item.entity.Hashtag;

import lombok.Getter;

@Getter
public class FlaskResponseDto {
    private final List<Long> itemIdList;
    private final List<String> titleList;
    private final List<String> imgUrlList;
    private final List<Integer> priceList;
    private final List<String> typeList;
    private final List<String> addressList;
    private final List<Hashtag> hashtagList;
    private final List<LocalDateTime> updatedAtList;

    public FlaskResponseDto(List<Long> itemIdList, 
    List<String> titleList, 
    List<String> imgUrlList, 
    List<Integer> priceList,
    List<String> typeList,
    List<String> addressList,
    List<Hashtag> hashtagList,
    List<LocalDateTime> updatedAtList) {
        this.itemIdList = itemIdList;
        this.titleList = titleList;
        this.imgUrlList = imgUrlList;
        this.priceList = priceList;
        this.typeList = typeList;
        this.addressList = addressList;
        this.hashtagList = hashtagList;
        this.updatedAtList = updatedAtList;
    }
}