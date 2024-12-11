package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatroomRepository chatroomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessage saveChatMessage(ChatMessageDto chatMessageDto) {
        Chatroom chatroom = chatroomRepository.findById(chatMessageDto.getChatRoomId())
                .orElseThrow(() -> new RuntimeException("Chatroom not found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .content(chatMessageDto.getMessage())
                .senderId(chatMessageDto.getSenderId())
                .chatroom(chatroom)
                .build();

        return chatMessageRepository.save(chatMessage);
    }
}
