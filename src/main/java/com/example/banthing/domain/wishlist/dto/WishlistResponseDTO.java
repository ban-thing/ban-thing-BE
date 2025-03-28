package com.example.banthing.domain.wishlist.dto;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class WishlistResponseDTO {

    private final Long itemId;
    private final String title;
    private final Integer price;
    private final String type;
    private final String address;
    private final List<String> imgUrls;
    private final LocalDateTime updatedAt;

    public WishlistResponseDTO(Item item) {
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.type = item.getType().toString();
        this.address = item.getAddress();
        this.imgUrls = item.getImages().stream()
                .map(ItemImg::getImgUrl)
                .collect(Collectors.toList());
        this.updatedAt = item.getUpdatedAt();
    }
}