package com.example.banthing.domain.chat.controller;

import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.dto.FindMessageAndItemResponseDto;
import com.example.banthing.domain.chat.dto.FindRoomsResponseDto;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.banthing.global.common.ApiResponse.successResponse;
import static com.example.banthing.global.common.ApiResponse.successWithDataAndMessage;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
@CrossOrigin(origins = "https://banthing.net")
public class ChatController {

    private final ChatroomService chatroomService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoomResponseDto>> createRoom(@AuthenticationPrincipal String userId,
                                                                         @RequestBody CreateRoomRequestDto request) {
        CreateRoomResponseDto res = chatroomService.createRoom(Long.parseLong(userId), request);
        return ResponseEntity.ok().body(successWithDataAndMessage(res, res.getMessage()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FindRoomsResponseDto>>> findAllRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(chatroomService.findAllRooms(Long.parseLong(userId))));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ApiResponse<FindMessageAndItemResponseDto>> getChatMessages(@PathVariable Long roomId,
                                                                                      Pageable pageable) {
        return ResponseEntity.ok().body(successResponse(chatroomService.getChatMessages(roomId, pageable)));
    }
}
