package com.example.banthing.domain.chat.controller;

import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.FindRoomsResponseDto;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // 채팅방 전체목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<FindRoomsResponseDto>>> findAllRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(chatroomService.findAllRooms(Long.parseLong(userId))));
    }

}
