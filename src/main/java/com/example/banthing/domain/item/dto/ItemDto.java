package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.user.entity.ProfileImage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ItemDto {

    private String title;
    private String content;
    private ProfileImage sellerImgUrl;
    private String sellerNickname;
    private ItemType type;
    private Integer price;
    private String directLocation;
    private String address;
    private List<String> itemImgs;
    private HashtagDto hashtag;
    private CleaningDetailDto cleaningDetail;
    private boolean isDirect;
    private LocalDateTime updateTime;

    public static ItemDto fromEntity(Item item) {
        return new ItemDto(
                item.getTitle(),
                item.getContent(),
                item.getSeller().getProfileImg(),
                item.getSeller().getNickname(),
                item.getType(),
                item.getPrice(),
                item.getDirectLocation(),
                item.getSeller().getAddress2(),
                item.getImages().stream()
                        .map(ItemImg::getImgUrl)
                        .collect(Collectors.toList()),
                (HashtagDto) item.getHashtags().stream()
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toList()),
                item.getCleaningDetail() != null ? CleaningDetailDto.fromEntity(item.getCleaningDetail()) : null,
                item.getIsDirect(),
                item.getUpdatedAt()
        );
    }

}

