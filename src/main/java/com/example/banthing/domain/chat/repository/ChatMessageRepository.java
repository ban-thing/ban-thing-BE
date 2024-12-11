package com.example.banthing.domain.chat.repository;

import com.example.banthing.domain.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT m FROM ChatMessage m WHERE m.chatroom.id = :roomId ORDER BY m.createdAt DESC")
    Slice<ChatMessage> findMessagesByChatroomId(@Param("roomId") Long roomId, Pageable pageable);

    Slice<ChatMessage> findAllByChatroomIdOrderByCreatedAtDesc(Long ChatroomId, Pageable page);
}
