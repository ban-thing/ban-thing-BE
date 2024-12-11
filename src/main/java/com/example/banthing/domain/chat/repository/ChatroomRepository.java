package com.example.banthing.domain.chat.repository;

import com.example.banthing.domain.chat.entity.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    List<Chatroom> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);

}
