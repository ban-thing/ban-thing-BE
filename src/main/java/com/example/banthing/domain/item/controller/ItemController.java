package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.CreateItemRequestDto;
import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemListResponseDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.repository.ItemRepository;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.global.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
//@CrossOrigin(origins = "https://banthing.net")
public class ItemController {

    private final ItemService itemService;
    public static Logger logger = LoggerFactory.getLogger("Item 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    

    /***
     *
     * 상품 등록
     *
     ***/
    @PostMapping(consumes = {org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<?>> addItem(
            @ModelAttribute CreateItemRequestDto request,
            @AuthenticationPrincipal String id
    ) throws IOException {
        return ResponseEntity.ok().body(successResponse(itemService.save(Long.valueOf(id), request)));
        //return null;
    }

    /***
     *
     * 상품 수정
     *
     ***/
    @PatchMapping(path = "/{itemId}", consumes = {org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @ModelAttribute CreateItemRequestDto request,
            @AuthenticationPrincipal String userId,
            @PathVariable Long itemId) throws IOException {
        logger.info("데이터 받기 성공");
        itemService.checkUserItem(itemId, userId);
        logger.info("데이터 보내기 시작");
        return ResponseEntity.ok().body(successResponse(itemService.update(Long.valueOf(itemId), request, userId)));
    }

    /***
     *
     * 상품 단건 조회
     *
     ***/
    @GetMapping("/{itemId}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemById(@PathVariable Long itemId) {
        itemService.checkItem(itemId);
        return ResponseEntity.ok().body(successResponse(itemService.get(Long.valueOf(itemId))));
    }

    /***
     *
     * 상품 삭제
     *
     ***/
    @DeleteMapping("/{itemId}")
    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal String userId) {
        itemService.checkUserItem(itemId, userId);
        itemService.delete(itemId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }

    /***
     *
     * 판매 완료
     *
     ***/
    @PatchMapping("/sell/{itemId}")
    public ResponseEntity<ApiResponse<?>> sellItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal String userId) {
        itemService.checkUserItem(itemId, userId);
        itemService.sell(itemId);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }

    /**
     *
     * 상품 검색
     *
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<ItemListResponseDto>> listItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String hashtags,
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) String address) {

        if (hashtags.length() != 0) {
            //return ResponseEntity.ok(successResponse(itemService.advancedListItems(keyword, hashtags, minPrice, maxPrice, address)));
            return ResponseEntity.ok(successResponse(itemService.listItems(keyword, minPrice, maxPrice, address)));
        } else {
            // or output FlaskResponseDto로 받는 방법 찾기

            return ResponseEntity.ok(successResponse(itemService.listItems(keyword, minPrice, maxPrice, address)));
        }
    }

}
