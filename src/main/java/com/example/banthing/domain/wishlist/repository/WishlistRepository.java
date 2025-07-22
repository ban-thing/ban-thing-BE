package com.example.banthing.domain.wishlist.repository;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.wishlist.entity.UserWishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<UserWishlist, Long> {

    boolean existsByUserAndItem(User user, Item item);

    Optional<UserWishlist> findByUserAndItem(User user, Item item);

    Optional<List<UserWishlist>> findByItem(Item item);

    List<UserWishlist> findByUser(User user);

    void deleteByUserId(Long userId);

    List<UserWishlist> findByUserId(Long userId);

    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}
