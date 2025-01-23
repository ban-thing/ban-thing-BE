package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemStatus;
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
    private ItemStatus status;
    private LocalDateTime updateTime;

    public static ItemDto fromEntity(Item item, ItemImgService itemImgService) {
        List<String> base64Images = itemImgService.getBase64EncodedImages(item.getId());
        List<String> imageNames = itemImgService.getImgNames(item.getId());
        return new ItemDto(
                item.getTitle(),
                item.getContent(),
                item.getSeller().getId(),
                item.getSeller().getProfileImg(),
                item.getSeller().getNickname(),
                item.getType(),
                item.getPrice(),
                item.getDirectLocation(),
                item.getAddress(),
                imageNames,
                base64Images,
                item.getHashtags().stream()
                        .map(HashtagDto::fromEntity)
                        .collect(Collectors.toList()),
                item.getCleaningDetail() != null ? CleaningDetailDto.fromEntity(item.getCleaningDetail()) : null,
                item.getIsDirect(),
                item.getStatus(),
                item.getUpdatedAt()
        );
    }
}

