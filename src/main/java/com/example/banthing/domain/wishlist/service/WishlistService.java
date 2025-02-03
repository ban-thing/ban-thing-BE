package com.example.banthing.domain.wishlist.service;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.domain.wishlist.dto.WishlistResponseDTO;
import com.example.banthing.domain.wishlist.entity.UserWishlist;
import com.example.banthing.domain.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    // 찜 추가 메서드
    public void requestWishlist(Long userId, Long itemId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        boolean exists = wishlistRepository.existsByUserAndItem(user, item);
        if (exists) {
            throw new IllegalStateException("이미 찜 목록에 추가된 아이템입니다.");
        }

        UserWishlist userWishlist = UserWishlist.builder()
                .user(user)
                .item(item)
                .build();

        wishlistRepository.save(userWishlist);
    }

    // 찜 삭제 메서드
    public void deleteWishlist(Long userId, Long itemId) {
        // 1. 사용자 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        // 2. 아이템 확인
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        // 3. Wishlist 존재 확인
        UserWishlist userWishlist = (UserWishlist) wishlistRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new IllegalArgumentException("찜 목록에 존재하지 않는 아이템입니다."));

        // 4. 삭제 처리
        wishlistRepository.delete(userWishlist);
    }

    public List<WishlistResponseDTO> findUserWishlist(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        List<UserWishlist> userWishlists = wishlistRepository.findByUser(user);

        return userWishlists.isEmpty()
                ? Collections.emptyList()
                : userWishlists.stream()
                .map(userWishlist -> new WishlistResponseDTO(userWishlist.getItem()))
                .toList();
    }

}

