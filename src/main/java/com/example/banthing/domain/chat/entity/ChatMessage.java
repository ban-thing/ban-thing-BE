package com.example.banthing.domain.chat.entity;

import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false)
    private Long senderId;

    @Builder
    public ChatMessage(String content, String imgUrl, boolean isRead, Chatroom chatroom, Long senderId) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.isRead = isRead;
        this.senderId = senderId;

        if (chatroom != null) {
            chatroom.addChatMessage(this); // Chatroom에 메시지를 추가
        }
    }

    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }
}
