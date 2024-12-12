package com.example.banthing.domain.user.dto;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.item.entity.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PurchaseResponseDto {

    private final Long itemId;
    private final String title;
    private final String imgUrl;
    private final Integer price;
    private final String type;
    private final String address;
    private final LocalDateTime updatedAt;

    public PurchaseResponseDto(Chatroom chatroom) {
        Item item = chatroom.getItem();
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.type = item.getType().toString();
        this.address = item.getAddress();
        this.updatedAt = item.getUpdatedAt();

        if (item.getImages().size() > 0)
            this.imgUrl = item.getImages().get(0).getImgUrl();
        else
            this.imgUrl = null;
    }
}
