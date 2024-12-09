package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.repository.HashtagRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final ItemRepository itemRepository;

    public void save(List<String> hashtags, Long itemId) throws IOException {
        List<Hashtag> saveHashtags = new ArrayList<>();
        for (String hashtagStr : hashtags) {
            Hashtag hashtag = Hashtag.builder()
                    .hashtag(hashtagStr)
                    .item(itemRepository.findById(itemId).orElseThrow())
                    .build();
            saveHashtags.add(hashtag);
        }
        hashtagRepository.saveAll(saveHashtags);
    }

    public void update(List<String> hashtags, Long itemId) throws IOException {

        List<Hashtag> deleteHashtags = hashtagRepository.findByItemId(itemId);
        hashtagRepository.deleteAll(deleteHashtags);
        save(hashtags,itemId);
    }
}
