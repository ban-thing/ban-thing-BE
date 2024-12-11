package com.example.banthing.domain.chat.repository;

import com.example.banthing.domain.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

}
