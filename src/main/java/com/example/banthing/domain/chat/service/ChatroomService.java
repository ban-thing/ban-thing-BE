package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public CreateRoomResponseDto createRoom(long userId, CreateRoomRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow();
        User seller = userRepository.findById(request.getSellerId()).orElseThrow();

        // 아이템 검증 로직 필요

        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .buyer(user)
                .seller(seller)
//              .item()
                .build());

        return new CreateRoomResponseDto(chatroom);
    }

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
