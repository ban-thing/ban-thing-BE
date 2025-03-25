package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    public void deleteBySenderId(Long userId) {
        chatMessageRepository.deleteBySenderId(userId);
    }

}
