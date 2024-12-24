package com.example.banthing.domain.user.dto;

import com.example.banthing.domain.user.entity.User;
import lombok.Getter;

import java.util.Base64;

@Getter
public class ProfileResponseDto {
    private final Long userId;
    private final String nickname;
    private final String email;
    private final String profileImg;
    private final String address1;
    private final String address2;
    private final String address3;

    public ProfileResponseDto(User user) {
        this.userId = user.getId();
        this.nickname = user.getNickname().contains("#") 
        ? user.getNickname().substring(0, user.getNickname().length() - 6)
        : user.getNickname();
        this.email = user.getEmail();
        this.profileImg = Base64.getEncoder().encodeToString(user.getProfileImg().getData());
        this.address1 = user.getAddress1();
        this.address2 = user.getAddress2();
        this.address3 = user.getAddress3();
    }
}
