package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.dto.*;
import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    public static Logger logger = LoggerFactory.getLogger("Chat 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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

        Optional<Chatroom> existingChatroom = chatroomRepository.findBySellerIdAndItemId(seller.getId(), item.getId());

        if (existingChatroom.isPresent()) {
            return new CreateRoomResponseDto(existingChatroom.get().getId(), "이미 방이 존재합니다.");
        } else {
            Chatroom savedChatroom = chatroomRepository
                    .save(Chatroom.builder()
                            .buyer(user)
                            .seller(seller)
                            .item(item)
                            .build()
                    );
            return new CreateRoomResponseDto(savedChatroom.getId(), "새로운 방이 생성되었습니다.");
        }
    }

    public List<FindRoomsResponseDto> findAllRooms(Long userId) {
        User user = findUserById(userId);

        return chatroomRepository
                .findRoomsByUserIdOrderByLatestMessage(userId)
                .stream()
                .map(chatroom -> new FindRoomsResponseDto(chatroom, user))
                .toList();
    }

    public FindMessageAndItemResponseDto getChatMessages(Long roomId, Pageable pageable) {
        Chatroom chatroom = chatroomRepository.findById(roomId)
                .orElseThrow(() -> new NullPointerException("해당 채팅방은 존재하지 않습니다."));

        Slice<ChatMessage> messages = chatMessageRepository.findAllByChatroomIdOrderByCreatedAtDesc(roomId, pageable);

        logger.info(new FindMessageAndItemResponseDto(chatroom, messages).getBuyer());
        
        return new FindMessageAndItemResponseDto(chatroom, messages);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }
}
