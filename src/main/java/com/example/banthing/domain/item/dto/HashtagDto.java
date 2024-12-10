package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.Hashtag;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashtagDto {

    private Long id;
    private String hashtag;

    public static HashtagDto fromEntity(Hashtag hashtagEntity) {
        return new HashtagDto(
                hashtagEntity.getId(),
                hashtagEntity.getHashtag()
        );
    }
}
