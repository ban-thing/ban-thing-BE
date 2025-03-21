package com.example.banthing.domain.user.entity;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.user.dto.UpdateAddressRequestDto;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    @Column(unique = true, nullable = false)
    private Long socialId;

    @Column(nullable = false)
    private String email;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "profile_image_id")
//    private ProfileImage profileImg;

    @JoinColumn(name = "profile_image_id")
    private String profileImg;

    private String address1;

    private String address2;

    private String address3;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> purchases = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Item> sales = new ArrayList<>();

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Chatroom> buyerChats = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Chatroom> sellerChats = new ArrayList<>();

    @Builder
    public User(String nickname, Long socialId, String email, String profileImg,
                String address1, String address2, String address3, LoginType loginType) {
        this.nickname = nickname;
        this.socialId = socialId;
        this.email = email;
        this.profileImg = profileImg;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.loginType = loginType;
    }

    public void updateAddress(UpdateAddressRequestDto request) {
        if (request.getAddress1() != null) this.address1 = request.getAddress1();
        if (request.getAddress2() != null) this.address2 = request.getAddress2();
        if (request.getAddress3() != null) this.address3 = request.getAddress3();
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname2() {
        return this.nickname.contains("#") 
                        ? this.nickname.substring(0, this.nickname.length() - 6)
                        : this.nickname;
    }
}
