package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.CreateItemRequestDto;
import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemListResponseDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    
    /***
     *
     * 상품 등록
     *
     ***/
    @PostMapping
    public ResponseEntity<ApiResponse<ItemResponseDto>> addItem(
            @ModelAttribute CreateItemRequestDto request,
            @AuthenticationPrincipal String id
    ) throws IOException {
        return ResponseEntity.ok().body(successResponse(itemService.save(Long.valueOf(id), request)));
    }

    /***
     *
     * 상품 수정
     *
     ***/
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemResponseDto>> updateItem(
            @PathVariable Long id,
            @RequestPart(value = "data", required = false) Map<String, Object> requestData,
            @RequestPart(value = "images", required = false) List<MultipartFile> newImages) throws IOException {

        return ResponseEntity.ok().body(successResponse(itemService.update(Long.valueOf(id), requestData, newImages)));
    }

    /***
     *
     * 상품 조회
     *
     ***/
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok().body(successResponse(itemService.get(Long.valueOf(id))));
    }

    /***
     *
     * 상품 삭제
     *
     ***/
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<?>> deleteItem(@PathVariable Long id) {
        itemService.delete(id);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }

    /***
     *
     * 판매 완료
     *
     ***/
    @PatchMapping("/sell/{id}")
    public ResponseEntity<ApiResponse<?>> sellItem(@PathVariable Long id) {
        itemService.sell(id);
        return ResponseEntity.ok(ApiResponse.successWithNoContent());
    }
    
    /**
     * 
     * 상품 검색
     * 
     */
    @GetMapping("")
    public ResponseEntity<ApiResponse<ItemListResponseDto>> listItems(
        @RequestParam(required = false ) String keyword,
        @RequestParam(required = false ) String hashtags,
        @RequestParam(required = false ) Long minPrice,
        @RequestParam(required = false ) Long maxPrice,
        @RequestParam(required = false ) String address) {
        
        if (hashtags.length() != 0) {
            
            //logger.atError();
            // output ItemListResponseDto로 받는 방법 찾기
            return ResponseEntity.ok(successResponse(itemService.advancedListItems(keyword, hashtags, minPrice, maxPrice, address))); 
        }else {
            // or output FlaskResponseDto로 받는 방법 찾기
            
            return ResponseEntity.ok(successResponse(itemService.listItems(keyword, minPrice, maxPrice, address)));
        }
        
        //return ResponseEntity.ok(itemService.listItems(page, keyword, filter_low, filter_high));
        
    }
    
}
