package com.example.banthing.domain.item.entity;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "items")
public class Item extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    private String address;

    private String directLocation;

    private boolean isDirect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @OneToMany(mappedBy = "item")
    private List<ItemImg> images = new ArrayList<>(); ;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cleaning_detail_id")
    private CleaningDetail cleaningDetail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    public Item(String title, String content, Integer price, ItemType type, ItemStatus status, String address,
                String directLocation, boolean isDirect, User buyer, User seller,
                CleaningDetail cleaningDetail, Hashtag hashtag) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.type = type;
        this.status = status;
        this.address = address;
        this.directLocation = directLocation;
        this.isDirect = isDirect;
        this.buyer = buyer;
        this.seller = seller;
        this.cleaningDetail = cleaningDetail;
        this.hashtag = hashtag;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public void addImage(ItemImg image) {
        this.images.add(image);
        image.setItem(this); // 양방향 연관관계 설정
    }
}
