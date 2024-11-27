package com.example.banthing.domain.item.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "item_imgs")
public class ItemImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Builder
    public ItemImg(String imgUrl, Item item) {
        this.imgUrl = imgUrl;
        this.item = item;
        item.addImage(this); // 양방향 연관관계 설정
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
