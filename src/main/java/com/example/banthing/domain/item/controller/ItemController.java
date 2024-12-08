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
import org.springframework.ui.Model;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/items") // Base URL for all handles in this controller
public class ItemController {

    private final ItemService itemService;
    //private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Handles GET requests to retrieve a list of items based on filters.
     * @param 
     * @param address 동위치
     * @return A ResponseEntity containing the filtered item list.
     */

    // 목록 조회
    @GetMapping("")
    public ResponseEntity<ItemListResponseDto> listItems(@RequestBody(required = true ) ItemSearchRequestDto request) {
        
        String keyword = request.getKeyword();
        List<String> hashtags = request.getHashtags();
        int filter_low = request.getFilter_low();
        int filter_high = request.getFilter_high();
        String address = request.getAddress();

        if (hashtags.size() != 0) {
            
            //logger.atError();
            // output ItemListResponseDto로 받는 방법 찾기
            return ResponseEntity.ok(itemService.advancedListItems(keyword, hashtags, filter_low, filter_high, address)); 
        }else {
            // or output FlaskResponseDto로 받는 방법 찾기
            
            return ResponseEntity.ok(itemService.listItems(keyword, filter_low, filter_high, address));
        }
        
        //return ResponseEntity.ok(itemService.listItems(page, keyword, filter_low, filter_high));
        
    }

}