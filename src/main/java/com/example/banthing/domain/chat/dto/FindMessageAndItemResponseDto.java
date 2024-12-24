package com.example.banthing.domain.chat.dto;

import com.example.banthing.domain.chat.entity.ChatMessage;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.item.entity.Item;
import lombok.Getter;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FindMessageAndItemResponseDto {

    private final Long itemId;
    private final String title;
    private final Integer price;
    private final String seller;
    private final String buyer;
    private String itemImage;
    private List<MessagesDto> messages = new ArrayList<>();
    private final boolean hasNext;

    public FindMessageAndItemResponseDto(Chatroom chatroom, Slice<ChatMessage> messages) {
        Item item = chatroom.getItem();
        this.itemId = item.getId();
        this.title = item.getTitle();
        this.price = item.getPrice();
        this.seller = item.getSeller().getNickname();
        this.buyer = chatroom.getBuyer().getNickname();
        if (item.getImages().size() > 0)
            this.itemImage = item.getImages().get(0).getImgUrl();

        this.messages = messages.getContent().stream()
                .map(MessagesDto::new)
                .collect(Collectors.toList());
        this.hasNext = messages.hasNext();
    }

    @Getter
    private static class MessagesDto {
        private final Long senderId;
        private final String message;
        private final LocalDateTime time;

        public MessagesDto(ChatMessage chatMessage) {
            this.senderId = chatMessage.getSenderId();
            this.message = chatMessage.getContent();
            this.time = chatMessage.getCreatedAt();
        }
    }
}
