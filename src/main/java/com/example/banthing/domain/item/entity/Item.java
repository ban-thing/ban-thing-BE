package com.example.banthing.domain.item.entity;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Getter
@Setter
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

    @Column(name = "address")
    private String address;

    private String directLocation;

    private Boolean isDirect;

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

    @OneToMany(mappedBy = "item")
    private List<Hashtag> hashtags = new ArrayList<>();

    @JoinColumn(name = "vectorized_hashtags1")
    private List<Long> vectorized_hashtags1;

    @JoinColumn(name = "vectorized_hashtags2")
    private List<Long> vectorized_hashtags2;

    @Builder(toBuilder = true)
    public Item(String title, String content, Integer price, ItemType type, ItemStatus status, String address,
                String directLocation, boolean isDirect, User buyer, User seller,
                CleaningDetail cleaningDetail, List<Long> vectorized_hashtags1, List<Long> vectorized_hashtags2) {
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
        this.vectorized_hashtags1 = vectorized_hashtags1;
        this.vectorized_hashtags2 = vectorized_hashtags2;
    }

    public void addImage(ItemImg image) {
        this.images.add(image);
        image.setItem(this); // 양방향 연관관계 설정
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        ZoneId koreanTimeZone = ZoneId.of("Asia/Seoul");
        updatedAt = LocalDateTime.now(koreanTimeZone);
    }

    @PreUpdate
    public void preUpdate() {
        ZoneId koreanTimeZone = ZoneId.of("Asia/Seoul");
        updatedAt = LocalDateTime.now(koreanTimeZone);
    }

}
