package com.example.banthing.domain.user.entity;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 사용
@Builder
@Table(name = "users")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private Long socialId;

    private String email;

    private String profileImgUrl;

    private String address1;

    private String address2;

    private String address3;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToMany(mappedBy = "buyer")
    private List<Item> purchases = new ArrayList<>(); // 구매목록

    @OneToMany(mappedBy = "seller")
    private List<Item> sales = new ArrayList<>(); // 판매목록

    @OneToMany(mappedBy = "buyer")
    private List<Chatroom> buyerChats = new ArrayList<>(); // 구매 채팅 리스트

    @OneToMany(mappedBy = "seller")
    private List<Chatroom> sellerChats = new ArrayList<>(); // 판매 채팅 리스트

    public void addPurchase(Item item) {
        this.purchases.add(item);
        item.setBuyer(this);
    }

    public void addSale(Item item) {
        this.sales.add(item);
        item.setSeller(this);
    }

    public void addBuyerChat(Chatroom chatroom) {
        this.buyerChats.add(chatroom);
        chatroom.setBuyer(this);
    }

    public void addSellerChat(Chatroom chatroom) {
        this.sellerChats.add(chatroom);
        chatroom.setSeller(this);
    }
}
