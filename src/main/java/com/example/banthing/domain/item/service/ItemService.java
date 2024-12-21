package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.dto.*;
import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.item.mapper.ItemMapper;
import com.example.banthing.domain.item.repository.CleaningDetailRepository;
import com.example.banthing.domain.item.repository.HashtagRepository;
import com.example.banthing.domain.item.repository.ItemImgRepository;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.banthing.domain.item.entity.ItemStatus.판매완료;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    public static Logger logger = LoggerFactory.getLogger("Item 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final ItemMapper itemMapper;
    
    @Value("${file.flask-url}")
    private String flask_url;

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgsService;
    private final UserRepository userRepository;
    private final CleaningDetailRepository cleaningDetailRepository;
    private final HashtagService hashtagService;
    private final HashtagRepository hashtagRepository;
    
    public ItemResponseDto save(Long id, CreateItemRequestDto request) throws IOException {
        logger.info("Request images size: {}", request.getImages() != null ? request.getImages().size() : "No images received");
        //MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        //User seller = userRepository.findById(id).orElseThrow(NullPointerException::new);

        //logger.info("cleaning detail in Service: {}", objectMapper.writeValueAsString(request));
        /*
        User seller = userRepository.findById(id).orElseThrow(NullPointerException::new);

        CleaningDetail cleaningDetail = cleaningDetailRepository.save(CleaningDetail.builder()
                .pollution(request.getClnPollution())
                .timeUsed(request.getClnTimeUsed())
                .purchasedDate(request.getClnPurchasedDate())
                .cleaned(request.getClnCleaned())
                .expire(request.getClnExpire())
                .build());

        //logger.info("cleaning detail in Service: {}", objectMapper.writeValueAsString(request));
        
        Item item = itemRepository.save(Item.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .price(request.getPrice())
                .type(ItemType.valueOf(request.getType()))
                .status(ItemStatus.판매중)
                .address(request.getAddress())
                .directLocation(request.getDirectLocation())
                .seller(seller)
                .cleaningDetail(cleaningDetail)
                .isDirect(request.getIsDirect())
                .build());
        
        hashtagService.save(request.getHashtags(), item.getId());
        itemImgsService.save(request.getImages(), item.getId());
         */
        return new ItemResponseDto(null);
        
    }

    public ItemResponseDto update(Long itemId, CreateItemRequestDto request, String userId)throws IOException {

        User seller = userRepository.findById(Long.valueOf(userId)).orElseThrow(RuntimeException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(RuntimeException::new);

        CleaningDetail cleaningDetail = cleaningDetailRepository.save(CleaningDetail.builder()
                .pollution(request.getClnPollution())
                .timeUsed(request.getClnTimeUsed())
                .purchasedDate(request.getClnPurchasedDate())
                .cleaned(request.getClnCleaned())
                .expire(request.getClnExpire())
                .build());
        cleaningDetailRepository.save(cleaningDetail);

        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setType(ItemType.valueOf(request.getType()));
        item.setPrice(request.getPrice());
        item.setDirectLocation(request.getDirectLocation());
        item.setAddress(request.getAddress());
        item.setSeller(seller);
        item.setIsDirect(request.getIsDirect());
        itemRepository.save(item);

        hashtagService.update(request.getHashtags(), item.getId());
        itemImgsService.update(request.getImages(), item.getId());

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
    }

    public void checkItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다.")
        );
    }

    public void checkUserItem(Long itemId, String userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));
        User user = userRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User를 찾을 수 없습니다"));

        if (!item.getSeller().equals(user)) {
            throw new AccessDeniedException("해당 item에 접근할 수 없습니다.");
        }
    }
    
    // 일반 & 필터 검색 메소드
    public ItemListResponseDto listItems(String keyword, Long minPrice, Long maxPrice, String address) {

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
        
        if (body == null || body.isEmpty()){
            return new ItemListResponseDto(body);
        }

        logger.info(flask_url);

        // send request to python with result, start, and end
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", "application/json");
        
        FlaskRequestDto request_body = new FlaskRequestDto(body);
        request_body.setHashtag(hashtags.toString());
        
        String flask_full_url = "http://" + flask_url + ":7000/post";
        HttpEntity<FlaskRequestDto> requestHttp = new HttpEntity<>(request_body, headers);
        ResponseEntity<List<FlaskItemResponseDto>> flask_response = restTemplate.exchange(
            flask_full_url, 
            HttpMethod.POST,
            requestHttp , 
            new ParameterizedTypeReference<List<FlaskItemResponseDto>>() {}); // fromFlask()로 매핑해야함
        
        // result = response from python server
        List<ItemSearchResponseDto> result = flask_response.getBody().stream()
        .map(ItemSearchResponseDto::fromFlask)
        .collect(Collectors.toList());
        
        try{
            logger.info("cleaning detail in Service: {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            logger.error("error", e);
        }

        return new ItemListResponseDto(result);

    }
}
