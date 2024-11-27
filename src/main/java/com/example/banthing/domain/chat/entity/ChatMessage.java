package com.example.banthing.domain.chat.entity;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_messages")
public class ChatMessage extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String imgUrl;

    private boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Builder
    public ChatMessage(String content, String imgUrl, boolean isRead, Chatroom chatroom, User sender) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.isRead = isRead;
        this.sender = sender;

        if (chatroom != null) {
            chatroom.addChatMessage(this); // Chatroom에 메시지를 추가
        }
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }
}
