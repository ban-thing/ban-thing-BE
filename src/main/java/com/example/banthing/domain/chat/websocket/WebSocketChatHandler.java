package com.example.banthing.domain.chat.websocket;


import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.service.ChatService;
import com.example.banthing.global.s3.S3Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper;
    private final ChatService chatService;

    private final Set<WebSocketSession> sessions = new HashSet<>();     // 소켓 세션을 저장
    private final Map<Long, Set<WebSocketSession>> chatRoomSessionMap = new HashMap<>();    // 채팅방 id와 소켓 세션

    private final S3Service s3Service;

    // 소켓 연결 확인
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("{} 연결됨", session.getId());
        sessions.add(session);

        // 클라이언트로부터 채팅방 ID를 받아서 입장 처리
        String uri = session.getUri().toString();
        String[] parts = uri.split("/");

        Long roomId = Long.parseLong(parts[parts.length - 1]);

        // 채팅방에 세션 추가
        chatRoomSessionMap.computeIfAbsent(roomId, s -> new HashSet<>()).add(session);
        log.info("채팅방 {}에 입장: {}", roomId, session.getId());

        session.sendMessage(new TextMessage("WebSocket 연결 완료"));
    }

    // 소켓 메세지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload {}", payload);

        // 클라이언트로부터 받은 메세지를 ChatMessageDto로 변환
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        byte[] imgBytes = Base64.getDecoder().decode(chatMessageDto.getData());
        
        // 이미지 저장
        String imageUrl = s3Service.uploadImageFromBytes("chatImage", chatMessageDto.getImgUrl(), imgBytes);

        chatMessageDto.setImgUrl(imageUrl);
        log.info("img Url:: {}", imageUrl);
        // 메시지 db 저장
        ChatMessageDto response = new ChatMessageDto(chatService.saveChatMessage(chatMessageDto));

        // 채팅 메세지 전송    
        for (WebSocketSession webSocketSession : chatRoomSessionMap.get(chatMessageDto.getChatRoomId())) {
            webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
        }

    }

    // 소켓 연결 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("{} 연결 끊김", session.getId());
        sessions.remove(session);

        // 모든 채팅방에서 해당 세션 제거
        chatRoomSessionMap.forEach((chatRoomId, sessionSet) -> sessionSet.remove(session));
        log.info("{} 세션이 모든 채팅방에서 제거되었습니다.", session.getId());
    }
}
