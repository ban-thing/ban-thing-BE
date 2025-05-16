package com.example.banthing.domain.chat.service;

import com.example.banthing.domain.chat.entity.ChatImg;
import com.example.banthing.domain.chat.repository.ChatImgRepository;
import com.example.banthing.domain.chat.repository.ChatMessageRepository;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.global.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatImgService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatImgService.class);
    private final ChatImgRepository chatImgsRepository;
    private final ChatroomRepository chatRoomRepository;
    private final S3Service s3Service;
    private String folderName = "chatImage";

    public void save(List<MultipartFile> images, Long chatroomId ) throws IOException {

        List<ChatImg> chatImgs = new ArrayList<>();
        String chatFolderPath = folderName + "/" + chatroomId; // 삭제시에 모든 파일 제거
        
        for (MultipartFile image: images) {

            String imgUrl = s3Service.uploadImage(chatFolderPath, image);
            ChatImg chatImg = ChatImg.builder()
                    .imgUrl(chatroomId + "/" + imgUrl)
                    .chatroom(chatRoomRepository.findById(chatroomId).orElseThrow())
                    .build();
            chatImgs.add(chatImg);

        }
        chatImgsRepository.saveAll(chatImgs);

    }

    public void deleteAll(Long chatroomId) {

        String chatFolderPath = folderName + "/" + chatroomId;
        s3Service.deleteFolder(chatFolderPath);
        List<ChatImg> chatImgs = chatImgsRepository.findByChatroomId(chatroomId);
        chatImgsRepository.deleteAll(chatImgs);

    }

    //update is not required

    public List<ChatImg> findChatImgs(Long chatroomId) {

        return chatImgsRepository.findByChatroomId(chatroomId);

    }

    public List<String> getImgNames(Long chatroomId) {

        return findChatImgs(chatroomId).stream()
                .map(ChatImg::getImgUrl)
                .collect(Collectors.toList());

    }

}
