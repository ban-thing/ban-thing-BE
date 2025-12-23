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
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
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
        item.addWishlist();
        itemRepository.save(item);
    }

    @Transactional
    public void deleteWishlist(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        UserWishlist userWishlist = wishlistRepository.findByUserAndItem(user, item)
                .orElseThrow(() -> new IllegalArgumentException("찜 목록에 존재하지 않는 아이템입니다."));

        wishlistRepository.delete(userWishlist);
        item.removeWishlist();
        itemRepository.save(item);
    }

    // 백엔드용
    @Transactional
    public void deleteAdminWishlist(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않습니다."));

        List<UserWishlist> userWishlists = wishlistRepository.findAllByItem(item)
                .orElseThrow(() -> new IllegalArgumentException("찜 목록에 존재하지 않는 아이템입니다."));

        for (int i = 0; i < userWishlists.size(); i++){
            wishlistRepository.delete(userWishlists.get(i));
        } 
        item.removeWishlist();
        itemRepository.save(item);
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

    @Transactional
    public void deleteByUserId(Long userId) {
        List<UserWishlist> userWishlists = wishlistRepository.findByUserId(userId);
        userWishlists.forEach(userWishlist -> {
            Item item = userWishlist.getItem();
            item.removeWishlist();
            itemRepository.save(item);
        });
        wishlistRepository.deleteByUserId(userId);
    }

    public boolean isItemWishlisted(Long userId, Long itemId) {
        return wishlistRepository.existsByUserIdAndItemId(userId, itemId);
    }
}
