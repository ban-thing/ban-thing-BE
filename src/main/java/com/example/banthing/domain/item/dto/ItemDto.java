package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.item.service.ItemImgService;
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
    private Long sellerId;
    private ProfileImage sellerImgUrl;
    private String sellerNickname;
    private ItemType type;
    private Integer price;
    private String directLocation;
    private String address;
    private List<String> itemImgNames;
    private List<String> itemImgs;
    private List<HashtagDto> hashtags;
    private CleaningDetailDto cleaningDetail;
    private boolean isDirect;
    private LocalDateTime updateTime;

    public static ItemDto fromEntity(Item item, ItemImgService itemImgService) {
        List<String> base64Images = itemImgService.getBase64EncodedImages(item.getId());
        List<String> imageNames = itemImgService.getImgNames(item.getId());
        return new ItemDto(
                item.getTitle(),
                item.getContent(),
                item.getSeller().getId(),
                item.getSeller().getProfileImg(),
                item.getSeller().getNickname().contains("#") 
                        ? item.getSeller().getNickname().substring(0, item.getSeller().getNickname().length() - 6)
                        : item.getSeller().getNickname(),
                item.getType(),
                item.getPrice(),
                item.getDirectLocation(),
                item.getSeller().getAddress2(),
                imageNames,
                base64Images,
                item.getHashtags().stream()
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toList()),
                item.getCleaningDetail() != null ? CleaningDetailDto.fromEntity(item.getCleaningDetail()) : null,
                item.getIsDirect(),
                item.getUpdatedAt()
        );
    }
}

