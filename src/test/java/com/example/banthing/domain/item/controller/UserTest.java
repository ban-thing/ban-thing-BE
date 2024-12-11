package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.user.entity.LoginType;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Transactional
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 생성 및 저장 테스트")
    void createAndSaveUser() {
        // 1. User 객체 생성
        User user = User.builder()
                .nickname("TestUser")
                .socialId(123456789L)
                .email("a")
                .profileImgUrl("a")
                .address1("Seoul")
                .address2("Gangnam")
                .address3("Station")
                .loginType(LoginType.kakao)
                .build();

        // 2. User 저장
        User savedUser = userRepository.save(user);

        // 3. 저장된 데이터 검증
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getNickname()).isEqualTo("TestUser");
        assertThat(savedUser.getEmail()).isEqualTo("testuser@example.com");
    }

    @Test
    @DisplayName("User와 Item 연관관계 테스트")
    void userItemRelationshipTest() {
        // 1. User 생성 및 저장
        User seller = User.builder()
                .nickname("Seller")
                .socialId(987654321L)
                .email("seller@example.com")
                .address1("Seoul")
                .address2("Mapo")
                .address3("Station")
                .loginType(LoginType.kakao)
                .build();
        userRepository.save(seller);

        // 2. Item 생성 및 User에 추가
        Item item = Item.builder()
                .title("Test Item")
                .content("This is a test item description")
                .price(10000)
                .type(ItemType.판매)
                .status(ItemStatus.판매중)
                .address("Test Address")
                .directLocation("Test Location")
                .seller(seller)
                .build();

        seller.addSale(item);

        // 3. 검증
        assertThat(seller.getSales().get(0).getTitle()).isEqualTo("Test Item");
        assertThat(seller.getSales().get(0).getSeller()).isEqualTo(seller);
    }
}