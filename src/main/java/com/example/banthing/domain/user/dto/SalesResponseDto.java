package com.example.banthing.domain.user.dto;

import com.example.banthing.domain.item.entity.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SalesResponseDto {

    private final Long itemId;
    private final String title;
    private final String imgUrl;
    private final Integer price;
    private final String status;
    private final String address;
    private final LocalDateTime updatedAt;

    public SalesResponseDto(Item item) {
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.status = item.getStatus().toString();
        this.address = item.getAddress();
        this.updatedAt = item.getUpdatedAt();

        if (item.getImages().size() > 0)
            this.imgUrl = item.getImages().get(0).getImgUrl();
        else
            this.imgUrl = null;
    }
}
