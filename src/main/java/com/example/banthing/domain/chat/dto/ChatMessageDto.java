package com.example.banthing.domain.chat.dto;

import com.example.banthing.domain.chat.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long chatRoomId;
    private Long senderId;
    private String message;
    private LocalDateTime time;
    private String imgUrl;
    private String data;

    public ChatMessageDto(ChatMessage chatMessage) {
        this.chatRoomId = chatMessage.getChatroom().getId();
        this.senderId = chatMessage.getSenderId();
        this.message = chatMessage.getContent();
        this.time = chatMessage.getUpdatedAt();
        this.imgUrl = chatMessage.getImgUrl();
        this.data = "";

    }

}
