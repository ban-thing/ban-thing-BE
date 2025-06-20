package com.example.banthing.domain.item.service;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.domain.item.dto.*;
import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ItemType;
import com.example.banthing.domain.item.mapper.ItemMapper;
import com.example.banthing.domain.item.repository.*;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.domain.wishlist.service.WishlistService;
import com.example.banthing.global.s3.S3Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
    private final ChatroomRepository chatroomRepository;
    private final ChatroomService chatroomService;
    private final S3Service s3Service;
    private final ItemImgRepository itemImgRepository;
    private final ItemReportRepository itemReportRepository;
    private final WishlistService wishlistService;

    public ItemResponseDto save(Long id, CreateItemRequestDto request) throws IOException {
//        logger.info("cleaning detail in Service: {}", objectMapper.writeValueAsString(request));

        User seller = userRepository.findById(id).orElseThrow(NullPointerException::new);

        CleaningDetail cleaningDetail = cleaningDetailRepository.save(CleaningDetail.builder()
                .pollution(request.getClnPollution())
                .timeUsed(request.getClnTimeUsed())
                .purchasedDate(request.getClnPurchasedDate())
                .cleaned(request.getClnCleaned())
                .expire(request.getClnExpire())
                .build());

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

        return new ItemResponseDto(item);
    }

    public ItemResponseDto update(Long itemId, CreateItemRequestDto request, String userId) throws IOException {

        logger.info("아이템 수정 시작");

        User seller = userRepository.findById(Long.valueOf(userId)).orElseThrow(RuntimeException::new);
        Item item = itemRepository.findById(itemId).orElseThrow(RuntimeException::new);

        logger.info("cleaning detail 저장 시작");

        CleaningDetail cleaningDetail = item.getCleaningDetail();
        cleaningDetail.setPollution(request.getClnPollution());
        cleaningDetail.setTimeUsed(request.getClnTimeUsed());
        cleaningDetail.setPurchasedDate(request.getClnPurchasedDate());
        cleaningDetail.setCleaned(request.getClnCleaned());
        cleaningDetail.setExpire(request.getClnExpire());
        cleaningDetailRepository.save(cleaningDetail);

        logger.info("일반형 파라미터 저장 시작");

        item.setTitle(request.getTitle());
        item.setContent(request.getContent());
        item.setType(ItemType.valueOf(request.getType()));
        item.setPrice(request.getPrice());
        item.setDirectLocation(request.getDirectLocation());
        item.setAddress(request.getAddress());
        item.setSeller(seller);
        item.setIsDirect(request.getIsDirect());

        logger.info("아이템저장 시작");

        itemRepository.save(item);

        logger.info("해시태그 저장 시작");

        hashtagService.update(request.getHashtags(), item.getId());

        logger.info("이미지 저장 시작");

        itemImgsService.update(request.getImages(), item.getId());

        return new ItemResponseDto(item);
    }

    public ItemDto get(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(RuntimeException::new);
        boolean isWishlisted = (userId != null) && wishlistService.isItemWishlisted(userId, item.getId());
        String base64ProfileImg = null;
        if (item.getSeller().getProfileImg() != null && !item.getSeller().getProfileImg().isEmpty()) {
            try {
                base64ProfileImg = s3Service.encodeImageToBase64(item.getSeller().getProfileImg(), "profileImage");
            } catch (IOException e) {
                throw new RuntimeException("Failed to encode profile image to Base64", e);
            }
        }
        List<String> base64Images = item.getImages().stream()
                .filter(Objects::nonNull)
                .map(img -> {
                    try {
                        return s3Service.encodeImageToBase64(img.getImgUrl(), "itemImage");
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to encode item image to Base64", e);
                    }
                })
                .collect(Collectors.toList());
        String nickname = item.getSeller().getNickname();
        String shortNickname = nickname.length() > 7 ? nickname.substring(0, 7) : nickname;
        return ItemDto.fromEntity(item, itemImgsService, base64ProfileImg, base64Images, isWishlisted, shortNickname);
    }

    public void delete(Long id) {
        //이미지 삭제
        itemImgsService.delete(id);

        //해시태그 삭제
        hashtagRepository.deleteAll(hashtagRepository.findByItemId(id));

        logger.info("채팅방 삭제 시작");
        //채팅방 삭제 
        // Retrieve the chatroom to be deleted
        Long seller_id = itemRepository.findById(id).orElseThrow(RuntimeException::new).getSeller().getId();
        List<Chatroom> chatrooms = chatroomRepository.findAllBySellerId(seller_id);

        for (Chatroom chatroom : chatrooms) {
            chatroomService.deleteRoom(chatroom.getId(), seller_id);
        }

        logger.info("채팅방 삭제 완료");

        Long cleaning_detail_id = itemRepository.findById(id).orElseThrow(RuntimeException::new).getCleaningDetail().getId();
        itemRepository.deleteById(id);

        logger.info("cleaning_detail 삭제 시작 {}", id);

        //cleaning detail 삭제
        cleaningDetailRepository.delete(cleaningDetailRepository.findById(cleaning_detail_id)
                .orElseThrow(() -> new IllegalArgumentException("CleaningDetail을 찾을 수 없습니다.")));

        logger.info("cleaning_detail 삭제 완료");

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

        if (minPrice == 0 && maxPrice == 0) {
            return new ItemListResponseDto(itemMapper.listItems(keyword, minPrice, maxPrice, address));
        } else {
            return new ItemListResponseDto(itemMapper.listFilteredItems(keyword, minPrice, maxPrice, address));
        }
    }

    // 상세 검색 메소드
    public ItemListResponseDto advancedListItems(String keyword, String hashtags, Long minPrice, Long maxPrice, String address) {

        List<ItemSearchResponseDto> body;
        if (minPrice == 0 && maxPrice == 0) {
            body = itemMapper.listItems(keyword, minPrice, maxPrice, address);
        } else {
            body = itemMapper.listFilteredItems(keyword, minPrice, maxPrice, address);
        }

        if (body == null || body.isEmpty()) {
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
                requestHttp,
                new ParameterizedTypeReference<List<FlaskItemResponseDto>>() {
                }); // fromFlask()로 매핑해야함

        // result = response from python server
        List<ItemSearchResponseDto> result = flask_response.getBody().stream()
                .map(ItemSearchResponseDto::fromFlask)
                .collect(Collectors.toList());

        try {
            logger.info("cleaning detail in Service: {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            logger.error("error", e);
        }

        return new ItemListResponseDto(result);

    }

    @Transactional
    public void deleteAllItemDataByUser(User user) {
        List<Item> items = itemRepository.findByBuyerOrSeller(user, user);
        for (Item item : items) {
            itemImgRepository.deleteByItem(item);
            hashtagRepository.deleteByItem(item);
            itemReportRepository.deleteByItem(item);
        }
        itemRepository.deleteAll(items);
    }

    public Optional<Item> findById(Long itemId) {
        return itemRepository.findById(itemId);
    }


}