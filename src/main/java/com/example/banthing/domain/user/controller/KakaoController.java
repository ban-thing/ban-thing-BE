package com.example.banthing.domain.user.controller;

import com.example.banthing.domain.user.dto.KakaoLoginResponseDto;
import com.example.banthing.domain.user.service.KakaoService;
import com.example.banthing.global.common.ApiResponse;
import com.example.banthing.global.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.banthing.global.common.ApiResponse.successWithDataAndMessage;
import static com.example.banthing.global.common.ApiResponse.successWithNoContent;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/user/kakao")
    public ResponseEntity<ApiResponse<?>> kakaoLogin(@RequestParam String token, HttpServletResponse response) throws JsonProcessingException {

        KakaoLoginResponseDto result = kakaoService.kakaoLogin(token);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);

        return ResponseEntity.ok().body(successWithDataAndMessage(result.getJwt(), result.getMessage()));
    }

    @GetMapping("/user/kakao/callback")
    public ResponseEntity<ApiResponse<?>> kakaoLoginForBe(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {

        String result = kakaoService.kakaoLoginForBe(code);
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, result);

        return ResponseEntity.ok().body(successWithNoContent());
    }
}
