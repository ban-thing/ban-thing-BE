package com.example.banthing.domain.chat.controller;

import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatroomService chatroomService;

    // 채팅방 생성
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoomResponseDto>> createRoom(@AuthenticationPrincipal String userId,
                                                                         @RequestBody CreateRoomRequestDto request) {
        return ResponseEntity.ok().body(successResponse(chatroomService.createRoom(Long.parseLong(userId), request)));
    }

}
