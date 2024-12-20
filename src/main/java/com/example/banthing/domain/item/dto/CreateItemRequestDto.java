package com.example.banthing.domain.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.example.banthing.domain.item.serialization.MultipartFileSerializer;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateItemRequestDto {

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

    // 이미지 파일
    // @JsonSerialize(using = MultipartFileSerializer.class)
    private List<MultipartFile> images;
}
