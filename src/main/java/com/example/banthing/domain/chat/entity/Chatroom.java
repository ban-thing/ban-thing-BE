package com.example.banthing.domain.chat.entity;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chatrooms")
public class Chatroom extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToMany(mappedBy = "chatroom")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatroom_images", fetch = FetchType.LAZY)
    private List<ChatImg> chatroomImgs = new ArrayList<>();

    @Builder
    public Chatroom(User buyer, User seller, Item item) {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void addChatMessage(ChatMessage chatMessage) {
        this.chatMessages.add(chatMessage);
        chatMessage.setChatroom(this);
    }

    public void addImage(ChatImg image) {

        this.chatroomImgs.add(image);
        image.setChatroom(this);

    }
}
