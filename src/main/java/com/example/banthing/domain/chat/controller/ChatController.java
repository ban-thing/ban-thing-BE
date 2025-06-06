/* 채팅방 관련 기능들을 제어하는 컨트롤러 그룹입니다. 
 * 해당 가능을 사용하기 위해 프론트엔드에선 api.banthing.net/chats 및 
 * 하위 링크로 요청을 보내거나 받을 수 있습니다.
 */

package com.example.banthing.domain.chat.controller;

import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.dto.CreateRoomRequestDto;
import com.example.banthing.domain.chat.dto.CreateRoomResponseDto;
import com.example.banthing.domain.chat.dto.FindMessageAndItemResponseDto;
import com.example.banthing.domain.chat.dto.FindRoomsResponseDto;
import com.example.banthing.domain.chat.service.ChatService;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.domain.chat.service.ChatImgService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.banthing.global.common.ApiResponse.successResponse;
import static com.example.banthing.global.common.ApiResponse.successWithDataAndMessage;


@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatroomService chatroomService;
    private final ChatService chatService;
    private final ChatImgService chatImgService;

    /* createRoom (userId, request)
     * 채팅방을 생성하는 기능입니다. 
     * 유저가 채팅방을 생성할 시 해당 유저의 userId를 가진 
     * 채팅방을 하나 생성합니다. CreateRoomRequestDto 타입을 가진
     * request 변수를 통해 sellerId, itemId등의 정보도 추가로 
     * 데이터베이스에 저장합니다. 
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateRoomResponseDto>> createRoom(@AuthenticationPrincipal String userId,
                                                                         @RequestBody CreateRoomRequestDto request) {
        CreateRoomResponseDto res = chatroomService.createRoom(Long.parseLong(userId), request);
        return ResponseEntity.ok().body(successWithDataAndMessage(res, res.getMessage()));
    }
 
    /* findAllRooms (userId)
     * 유저가 활성화된 채팅방을 확인할 떄 사용되는 기능입니다.
     * userId에 해당하는 채팅방들을 불러옵니다.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FindRoomsResponseDto>>> findAllRooms(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(chatroomService.findAllRooms(Long.parseLong(userId))));
    }


    /* getChatMessages (roomId, pageable)
     * roomId에 해당하는 메시지들을 데이터베이스에서 불러옵니다.
     * 소켓 세션이 새로고침 되거나, 메시지를 보낼 경우, 보낸이와 받은이의 
     * 소켓에서 해당 기능이 발동되고 메시지를 송출합니다.
     */
    @GetMapping(value = "/{roomId}")
    public ResponseEntity<ApiResponse<FindMessageAndItemResponseDto>> getChatMessages(@PathVariable Long roomId,
                                                                                      Pageable pageable) {
                                                                                        
        return ResponseEntity.ok().body(successResponse(chatroomService.getChatMessages(roomId, pageable)));
    }

    /* Dummy function
     * Undefined function that does nothing but takes the request from the front end
     * The previous employee installed this feature for some reason.
     * Later, figured it out that it was a dummy function. Will be elimincated in a near future
     * 
     */
    @PostMapping(value = "/{roomId}/message")
    public ResponseEntity<?> postToChatRoom(@PathVariable Long roomId, @RequestBody ChatMessageDto messageDto) {
        // image save
        
        
        return ResponseEntity.ok().body("Message received");
    }

}
