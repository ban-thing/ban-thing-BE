package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.entity.ItemType;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class ItemResponseDto {

    private Long itemId;
    private String title;
    private Integer price;
    private ItemType type;
    private String address;
    private Hashtag hashtags;
    private LocalDateTime updatedAt;
    private String images;

    public ItemResponseDto(Item item) {
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.type = item.getType();
        this.address = item.getAddress();
        this.hashtags = item.getHashtag();
        this.updatedAt = item.getUpdatedAt();
        
        if (item.getImages().size() > 0)
            this.images = item.getImages().get(0).getImgUrl();
        else 
            this.images = null;
        
    }
    
}
