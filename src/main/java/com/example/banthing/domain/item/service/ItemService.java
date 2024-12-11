package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.dto.ItemSearchRequestDto;
import com.example.banthing.domain.item.dto.ItemSearchResponseDto;
import com.example.banthing.domain.item.dto.ItemListResponseDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.dto.FlaskRequestDto;
import com.example.banthing.domain.item.dto.FlaskItemResponseDto;
import com.example.banthing.domain.item.mapper.ItemMapper;
import com.example.banthing.domain.item.entity.*;
import com.example.banthing.domain.user.entity.User;

import com.example.banthing.domain.item.repository.CleaningDetailRepository;
import com.example.banthing.domain.item.repository.HashtagRepository;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.example.banthing.domain.item.entity.ItemStatus.판매완료;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    public static Logger logger = LoggerFactory.getLogger("Flask 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ItemMapper itemMapper;

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


    public void sell(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(RuntimeException::new);
        item.setStatus(판매완료);
        itemRepository.save(item);
  
    // 일반 & 필터 검색 메소드
    public ItemListResponseDto listItems(String keyword, Long minPrice, Long maxPrice, String address) {
        
        try {
            logger.info("JSON Body1: {}", objectMapper.writeValueAsString(itemMapper.listItems(keyword, minPrice, maxPrice, address)));
            logger.info("JSON request Body1: {}", objectMapper.writeValueAsString(keyword));
            logger.info("filter low Body1: {}", objectMapper.writeValueAsString(minPrice));
            logger.info("filter high Body1: {}", objectMapper.writeValueAsString(maxPrice));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize items to JSON", e);
        }

        if( minPrice == 0 && maxPrice == 0){
            return new ItemListResponseDto(itemMapper.listItems(keyword, minPrice, maxPrice, address));
        } else {
            return new ItemListResponseDto(itemMapper.listFilteredItems(keyword, minPrice, maxPrice, address));
        }
    }

    // 상세 검색 메소드
    public ItemListResponseDto advancedListItems(String keyword, String hashtags, Long minPrice, Long maxPrice, String address) {
        
        List<ItemSearchResponseDto> body;
        if( minPrice == 0 && maxPrice == 0){
            body = itemMapper.listItems(keyword, minPrice, maxPrice, address);
        } else {
            body = itemMapper.listFilteredItems(keyword, minPrice, maxPrice, address);
        }
        
        
        try {
            logger.info("JSON Body2: {}", objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize items to JSON", e);
        }

        // send request to python with result, start, and end
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json");
        
        FlaskRequestDto request_body = new FlaskRequestDto(body);
        request_body.setHashtag(hashtags.toString());
        
        try {
            logger.info("JSON Flask requeset Body2: {}", objectMapper.writeValueAsString(request_body));
        } catch (JsonProcessingException e) {
            logger.error("JSON Flask requeset Failed to serialize items to JSON", e);
        }

        HttpEntity<FlaskRequestDto> requestHttp = new HttpEntity<>(request_body, headers);
        ResponseEntity<List<FlaskItemResponseDto>> flask_response = restTemplate.exchange(
            "http://localhost:7000/post", 
            HttpMethod.POST,
            requestHttp , 
            new ParameterizedTypeReference<List<FlaskItemResponseDto>>() {}); // fromFlask()로 매핑해야함
        
        // result = response from python server
        List<ItemSearchResponseDto> result = flask_response.getBody().stream()
        .map(ItemSearchResponseDto::fromFlask)
        .collect(Collectors.toList());
        
        return new ItemListResponseDto(result);

    }
}
