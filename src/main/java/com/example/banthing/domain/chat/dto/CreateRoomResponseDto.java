package com.example.banthing.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateRoomResponseDto {

    private final Long chatRoomId;
    private final String message;

}