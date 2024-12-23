package com.example.banthing.domain.chat.dto;

import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@NoArgsConstructor
public class FindRoomsResponseDto {
    private Long chatRoomId;
    private String address;
    private String itemImg;
    private String nickname;
    private String latestMessage;
    private LocalDateTime latestMessageDateTime;
    private Integer unreadMessageCount;
    private String type; // 판매/구매

    public FindRoomsResponseDto(Chatroom chatroom, User user) {
        this.chatRoomId = chatroom.getId();
        this.address = chatroom.getItem().getAddress();

        if (user == chatroom.getSeller()) {
            this.type = "판매";
            this.nickname = chatroom.getBuyer().getNickname();
        } else {
            this.type = "구매";
            this.nickname = chatroom.getSeller().getNickname();
        }

        if (chatroom.getItem().getImages().size() > 0) {
            this.itemImg = chatroom.getItem().getImages().get(0).getImgUrl();
        }

        if (chatroom.getChatMessages().size() > 0) {
            ChatMessage message = chatroom.getChatMessages().get(chatroom.getChatMessages().size() - 1);
            this.latestMessage = message.getContent();
            this.latestMessageDateTime = message.getCreatedAt();
        }
    }
}
