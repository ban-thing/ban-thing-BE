package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.entity.*;
import com.example.banthing.domain.item.repository.CleaningDetailRepository;
import com.example.banthing.domain.item.repository.HashtagRepository;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgsRepository;
    private final ItemImgService itemImgsService;
    private final UserRepository userRepository;
    private final CleaningDetailRepository cleaningDetailRepository;
    private final HashtagService hashtagService;
    private final HashtagRepository hashtagRepository;

    public ItemResponseDto save(Long id, Map<String, Object> requestData, List<MultipartFile> images) throws IOException {

        Long userId = Long.valueOf(id);
        User seller = userRepository.findById(userId).orElseThrow(RuntimeException::new);

        CleaningDetail cleaningDetail = cleaningDetailRepository.save(CleaningDetail.builder()
                .pollution((String) requestData.get("clnPollution"))
                .timeUsed((String) requestData.get("clnTimeUsed"))
                .purchasedDate((String) requestData.get("clnPurchasedDate"))
                .cleaned((String) requestData.get("clnCleaned"))
                .expire((String) requestData.get("clnExpire"))
                .build());


        Item item = itemRepository.save(Item.builder()
                .title((String) requestData.get("title"))
                .content((String) requestData.get("content"))
                .price((Integer) requestData.get("price"))
                .type(ItemType.valueOf((String) requestData.get("type")))
                .status(ItemStatus.판매중)
                .address((String) requestData.get("address"))
                .directLocation((String) requestData.get("directLocation"))
                .seller(seller)
                .cleaningDetail(cleaningDetail)
                .isDirect((Boolean) requestData.get("isDirect"))
                .build());

        List<String> hashtags = (List<String>) requestData.get("hashtags");
        hashtagService.save(hashtags, item.getId());
        itemImgsService.save(images, item.getId());

        return new ItemResponseDto(item);
    }

    public ItemResponseDto update(Long id, Map<String, Object> requestData, List<MultipartFile> images) throws IOException {

        Long userId = Long.valueOf(id);
        User seller = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        Item item = itemRepository.findById(id).orElseThrow(RuntimeException::new);

        CleaningDetail cleaningDetail = item.getCleaningDetail();
        cleaningDetail.setPollution((String) requestData.get("clnPollution"));
        cleaningDetail.setTimeUsed((String) requestData.get("clnTimeUsed"));
        cleaningDetail.setPurchasedDate((String) requestData.get("clnPurchasedDate"));
        cleaningDetail.setCleaned((String) requestData.get("clnCleaned"));
        cleaningDetail.setExpire((String) requestData.get("clnExpire"));
        cleaningDetailRepository.save(cleaningDetail);

        List<String> newHashtags = (List<String>) requestData.get("hashtags");
        hashtagService.update(newHashtags, item.getId());

        item.setTitle((String) requestData.get("title"));
        item.setContent((String) requestData.get("content"));
        item.setType(ItemType.valueOf((String) requestData.get("type")));
        item.setPrice((Integer) requestData.get("price"));
        item.setDirectLocation((String) requestData.get("directLocation"));
        item.setAddress((String) requestData.get("address"));
        item.setStatus(ItemStatus.valueOf((String) requestData.get("status")));
        item.setSeller(seller);
        item.setCleaningDetail(cleaningDetail);
        item.setIsDirect((Boolean) requestData.get("isDirect"));

        itemRepository.save(item);
        itemImgsService.update(images, item.getId());

        return new ItemResponseDto(item);
    }

    public ItemDto get(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(RuntimeException::new);
        return ItemDto.fromEntity(item);
    }

    public void delete(Long id) {
        itemImgsService.delete(id);
        cleaningDetailRepository.delete(cleaningDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CleaningDetail을 찾을 수 없습니다.")));
        hashtagRepository.deleteAll(hashtagRepository.findByItemId(id));
        itemRepository.deleteById(id);
    }
}
