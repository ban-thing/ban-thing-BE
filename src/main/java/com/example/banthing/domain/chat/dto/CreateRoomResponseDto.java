package com.example.banthing.domain.chat.dto;

import com.example.banthing.domain.chat.entity.Chatroom;
import lombok.Getter;

@Getter
public class CreateRoomResponseDto {
    private final Long chatRoomId;

    public CreateRoomResponseDto(Chatroom chatroom) {
        this.chatRoomId = chatroom.getId();
    }
}