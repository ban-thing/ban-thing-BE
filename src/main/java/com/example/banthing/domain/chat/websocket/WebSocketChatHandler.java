package com.example.banthing.domain.chat.websocket;


import com.example.banthing.domain.chat.dto.ChatMessageDto;
import com.example.banthing.domain.chat.service.ChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
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
        
        JsonNode jsonNode = mapper.readTree(payload);
        Long chatRoomId = jsonNode.get("chatRoomId").asLong();
        String sender = jsonNode.get("sender").asText();
        String messageText = jsonNode.has("message") ? jsonNode.get("message").asText() : "";
        String imageBase64 = jsonNode.has("image") ? jsonNode.get("image").asText() : null;

        // 클라이언트로부터 받은 메세지를 ChatMessageDto로 변환
        ChatMessageDto chatMessageDto = mapper.readValue(payload, ChatMessageDto.class);
        log.info("session {}", chatMessageDto.toString());

        // 이미지 여부 확인 
        List<String> imageList = new ArrayList<>();
        if (jsonNode.has("images")) {
            for (JsonNode imageNode : jsonNode.get("images")) {
                imageList.add(imageNode.asText());
            }
        }

        chatMessageDto.setImages(imageList);


        // 메시지 db 저장
        ChatMessageDto response = new ChatMessageDto(chatService.saveChatMessage(chatMessageDto));

        // 메세지 전송
        String responseJson = mapper.writeValueAsString(response);
        TextMessage responseMessage = new TextMessage(responseJson);

        // 채팅 메세지 전송
        if (chatRoomSessionMap.containsKey(chatRoomId)) {
            for (WebSocketSession webSocketSession : chatRoomSessionMap.get(chatMessageDto.getChatRoomId())) {
                webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(response)));
            }
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
