package com.example.banthing.domain.item.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateErrorDto {
    // Item 관련 필드
    private String title;
    private String content;
    private Integer price;
    private String type;
    private String address;
    private String directLocation;
    private Boolean isDirect;

    // CleaningDetail 관련 필드
    private String clnPollution;
    private String clnTimeUsed;
    private String clnPurchasedDate;
    private String clnCleaned;
    private String clnExpire;

    // 해시태그
    private List<String> hashtags;

    public CreateErrorDto(CreateItemRequestDto item){
        this.title = item.getTitle();
        this.content = item.getContent();
        this.price = item.getPrice();
        this.type = item.getType();
        this.address = item.getAddress();
        this.directLocation = item.getDirectLocation();
        this.isDirect = item.getIsDirect();
        this.clnPollution = item.getClnPollution();
        this.clnTimeUsed = item.getClnTimeUsed();
        this.clnPurchasedDate = item.getClnPurchasedDate();
        this.clnCleaned = item.getClnCleaned();
        this.clnExpire = item.getClnExpire();
        this.hashtags = item.getHashtags();

    }

}
