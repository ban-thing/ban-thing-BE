package com.example.banthing.domain.chat.repository;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
                SELECT c
                FROM Chatroom c
                LEFT JOIN c.chatMessages m
                WHERE c.buyer.id = :userId OR c.seller.id = :userId
                GROUP BY c
                ORDER BY MAX(m.createdAt) DESC
            """)
    List<Chatroom> findRoomsByUserIdOrderByLatestMessage(@Param("userId") Long userId);

    Optional<Chatroom> findBySellerIdAndItemId(Long selleId, Long itemId);

    @Query("""
                SELECT c
                FROM Chatroom c
                WHERE c.seller.id = :sellerId AND c.buyer.id = :buyerId AND c.item.id = :itemId
            """)
    Optional<Chatroom> findBySellerIdAndBuyerIdAndItemId(@Param("sellerId") Long sellerId, 
                                                        @Param("buyerId") Long buyerId, 
                                                        @Param("itemId") Long itemId);

    List<Chatroom> findAllByBuyerId(Long buyerId);

    List<Chatroom> findAllBySellerId(Long sellerId);

    void deleteByBuyerOrSeller(User buyer, User seller);
}
