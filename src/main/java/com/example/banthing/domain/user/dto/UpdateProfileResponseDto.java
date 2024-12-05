package com.example.banthing.domain.user.dto;

import com.example.banthing.domain.user.entity.User;
import lombok.Getter;

import java.util.Base64;

@Getter
public class UpdateProfileResponseDto {

    private final String nickname;
    private final String profileImg;

    public UpdateProfileResponseDto(User user) {
        this.nickname = user.getNickname();
        this.profileImg = Base64.getEncoder().encodeToString(user.getProfileImg().getData());
    }
}
