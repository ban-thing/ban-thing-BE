package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.item.service.ItemImgService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String address;
    private Integer price;
    private String title;
    private ItemType type;
    private List<HashtagDto> hashtag;
    private String images;
    private ItemStatus status;

    private static ItemImgService itemImgService;

    public ItemSearchResponseDto(ItemSearchResponseDto item) {
        this.id = item.getId();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
        this.address = item.getAddress();
        this.price = item.getPrice();
        this.title = item.getTitle();
        this.type = item.getType();
        this.hashtag = item.getHashtag();
        this.images = item.getImages();
        this.status = item.getStatus();
    }

    public static ItemSearchResponseDto fromEntity(Item item) {
        return new ItemSearchResponseDto(
            item.getId(),
            item.getCreatedAt(),
            item.getUpdatedAt(),
            item.getAddress(),
            item.getPrice(),
            item.getTitle(),
            item.getType(),
            item.getHashtags().stream()
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toList()),
            item.getImages().stream()
            .map(ItemImg::getImgUrl)
            .collect(Collectors.toList()).get(0),
            item.getStatus()

        );
        
    }

    public static ItemSearchResponseDto fromFlask(FlaskItemResponseDto item) {
        
        ItemSearchResponseDto newResponse = new ItemSearchResponseDto(
            item.getId(),
            item.getCreatedAt(),
            item.getUpdatedAt(),
            item.getAddress(),
            item.getPrice(),
            item.getTitle(),
            item.getType(),
            null,
            item.getImages(),
            item.getStatus()
        );
        
        List<HashtagDto> list_temp = new ArrayList<>();
        for (String text : item.getHashtag().split(", ")) {

            HashtagDto item_temp = new HashtagDto(null, text);
            list_temp.add(item_temp);
            
        }

        newResponse.setHashtag(list_temp);
        return new ItemSearchResponseDto(newResponse);
        
    }

}