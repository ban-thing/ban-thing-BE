package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.dto.ItemListResponseDto;
import com.example.banthing.domain.item.dto.ItemResponseDto;
import com.example.banthing.domain.item.dto.FlaskRequestDto;
import com.example.banthing.domain.item.dto.FlaskResponseDto;
import com.example.banthing.domain.item.mapper.ItemMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

@Service
public class ItemService {

    private final ItemMapper itemMapper;
    public static Logger logger = LoggerFactory.getLogger("Flask 관련 로그");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ItemService(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }
    
    public ItemListResponseDto listItems(String keyword, int filter_low, int filter_high, String address) {

        try {
            logger.info("JSON Body1: {}", objectMapper.writeValueAsString(itemMapper.listItems(keyword, filter_low, filter_high, address)));
            logger.info("JSON request Body1: {}", objectMapper.writeValueAsString(keyword));
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            logger.error("Failed to serialize items to JSON", e);
        }

        return new ItemListResponseDto(itemMapper.listItems(keyword, filter_low, filter_high, address));
    }

    public ItemListResponseDto advancedListItems(String keyword, List<String> hashtags, int filter_low, int filter_high, String address) {

        List<ItemResponseDto> body = itemMapper.listItems(keyword, filter_low, filter_high, address);
        
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
        ResponseEntity<List<ItemResponseDto>> flask_response = restTemplate.exchange(
            "http://localhost:7000/post", 
            HttpMethod.POST,
            requestHttp , 
            new ParameterizedTypeReference<List<ItemResponseDto>>() {});
        
        // result = response from python server
        List<ItemResponseDto> result = flask_response.getBody();
        
        return new ItemListResponseDto(result);
    }
}
