package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.ItemListResponseDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.dto.ItemSearchRequestDto;
import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.dto.ItemDto;
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
            @RequestPart("data") Map<String, Object> requestData,
            @RequestPart("images") List<MultipartFile> image,
            @AuthenticationPrincipal String id
    ) throws IOException {
        return ResponseEntity.ok().body(successResponse(itemService.save(Long.valueOf(id), requestData, image)));
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
    public void deleteItem(@PathVariable Long id) {
        itemService.delete(id);
    }

    /**
     * 
     * 상품 검색
     * 
     */
    @GetMapping("")
    public ResponseEntity<ItemListResponseDto> listItems(@RequestBody(required = true ) ItemSearchRequestDto request) {
        
        String keyword = request.getKeyword();
        String hashtags = request.getHashtags();
        int filter_low = request.getFilter_low();
        int filter_high = request.getFilter_high();
        String address = request.getAddress();

        if (hashtags.length() != 0) {
            
            //logger.atError();
            // output ItemListResponseDto로 받는 방법 찾기
            return ResponseEntity.ok(itemService.advancedListItems(keyword, hashtags, filter_low, filter_high, address)); 
        }else {
            // or output FlaskResponseDto로 받는 방법 찾기
            
            return ResponseEntity.ok(itemService.listItems(keyword, filter_low, filter_high, address));
        }
        
        //return ResponseEntity.ok(itemService.listItems(page, keyword, filter_low, filter_high));
        
    }

    
    @GetMapping("a")
    public ResponseEntity<ApiResponse<ItemDto>> testItems(@RequestBody(required = true ) ItemSearchRequestDto request) {
        String keyword = request.getKeyword();
        String hashtags = request.getHashtags();
        int filter_low = request.getFilter_low();
        int filter_high = request.getFilter_high();
        String address = request.getAddress();

        int test = 13;
        long longvalue = (long) test;


        return ResponseEntity.ok().body(successResponse(itemService.get(Long.valueOf(longvalue))));
    
    }
    
}
