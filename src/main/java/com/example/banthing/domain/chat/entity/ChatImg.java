package com.example.banthing.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_imgs")
public class ChatImg {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgUrl;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @Builder
    public ChatImg(String imgUrl, Chatroom chatroom) {

        this.imgUrl = imgUrl;
        this.chatroom = chatroom;
        chatroom.addImage(this); // 양방향 연관관계 설정

    }

}
