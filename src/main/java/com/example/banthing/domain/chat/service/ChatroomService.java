package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.dto.FindRoomsResponseDto;
import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public CreateRoomResponseDto createRoom(long userId, CreateRoomRequestDto request) {
        User user = findUserById(userId);
        User seller = findUserById(request.getSellerId());

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NullPointerException("해당 상품이 존재하지 않습니다."));

        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .buyer(user)
                .seller(seller)
                .item(item)
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

    public List<FindRoomsResponseDto> findAllRooms(Long userId) {
        User user = findUserById(userId);

        return chatroomRepository
                .findRoomsByUserIdOrderByLatestMessage(userId)
                .stream()
                .map(chatroom -> new FindRoomsResponseDto(chatroom, user))
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }
}
