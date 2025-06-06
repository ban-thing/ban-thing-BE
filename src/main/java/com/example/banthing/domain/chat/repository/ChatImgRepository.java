package com.example.banthing.domain.chat.repository;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.entity.ChatImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatImgRepository extends JpaRepository<ChatImg, Long> {

    List<ChatImg> findByChatroomId(Long chatRoomId);

    void deleteByChatroom(Chatroom chatRoom);
    
} 
