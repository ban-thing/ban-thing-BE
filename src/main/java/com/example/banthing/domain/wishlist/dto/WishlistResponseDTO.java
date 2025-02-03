package com.example.banthing.domain.wishlist.dto;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.item.entity.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class WishlistResponseDTO {

    private final Long itemId;
    private final String title;
    private final Integer price;
    private final String type;
    private final String address;
//    private final String imgUrl;
    private final LocalDateTime updatedAt;

    public WishlistResponseDTO(Item item) {
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.type = item.getType().toString();
        this.address = item.getAddress();
//        this.imgUrl = item
        this.updatedAt = item.getUpdatedAt();
    }
}