package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
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
}
