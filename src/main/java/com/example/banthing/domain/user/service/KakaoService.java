package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.dto.KakaoLoginResponseDto;
import com.example.banthing.domain.user.dto.KakaoUserInfoDto;
import com.example.banthing.domain.user.dto.SignUpResponseDto;
import com.example.banthing.domain.user.entity.LoginType;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserStatus;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.global.security.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Random;

@Slf4j(topic = "KAKAO Login")
@Service
@RequiredArgsConstructor
public class KakaoService {

    public static Logger logger = LoggerFactory.getLogger("로그인 관련 로그");

    private final UserRepository userRepository;
    private final RejoinRestrictionService rejoinRestrictionService;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @Value("${kakao.apikey}")
    private String apiKey;

    @Value("${kakao.redirect-uri}") // Base64 Encode 한 SecretKey
    private String redirectUri;

    public KakaoLoginResponseDto kakaoLogin(String accessToken) throws JsonProcessingException {

        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        SignUpResponseDto signUpResponse = registerKakaoUserIfNeeded(kakaoUserInfo);

        return new KakaoLoginResponseDto(jwtUtil.createToken(String.valueOf(signUpResponse.getUserId())),
                signUpResponse.getMessage());
    }

    public String kakaoLoginForBe(String code) throws JsonProcessingException {

        String accessToken = getToken(code);
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        SignUpResponseDto signUpResponse = registerKakaoUserIfNeeded(kakaoUserInfo);

        return jwtUtil.createToken(String.valueOf(signUpResponse.getUserId()));
    }

    // 액세스 토큰 요청
    private String getToken(String code) throws JsonProcessingException {

        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", apiKey);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    // 사용자 정보 요청
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("accessToken : " + accessToken);

        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        log.info("카카오 사용자 정보: " + id + " " + email);
        return new KakaoUserInfoDto(id, email);
    }

    // 회원가입 처리
    private SignUpResponseDto registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();

        if (rejoinRestrictionService.existsBySocialId(kakaoId)) {
            throw new IllegalArgumentException("해당 계정은 재가입이 제한되었습니다.");
        }

        logger.info("kakao Id" + kakaoId);

        User kakaoUser = userRepository.findBySocialId(kakaoId).orElse(null);

        if (kakaoUser != null && kakaoUser.getUserStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("정지된 계정입니다.");
        }

        // 신규 회원가입
        if (kakaoUser == null) {
            logger.info("신규 회원가입");
            String email = kakaoUserInfo.getEmail();
            
            kakaoUser = userRepository.save(User.builder()
                    .nickname("반띵#" + kakaoUserInfo.getId())
                    .email(email)
                    .socialId(kakaoId)
                    .profileImg(getRandomDefaultProfileImage())
                    .loginType(LoginType.kakao)
                    .build());
            log.info("회원가입");
            kakaoUser.updateLastLoginAt(LocalDateTime.now());
            userRepository.save(kakaoUser);
            return new SignUpResponseDto(kakaoUser.getId(), "회원가입 되었습니다");
        } else {
            logger.info("기존 회원 로그인");
            kakaoUser.updateLastLoginAt(LocalDateTime.now());
            userRepository.save(kakaoUser);

        }
        log.info("로그인 userId: " + kakaoUser.getId() + ", name: " + kakaoUser.getNickname());
        return new SignUpResponseDto(kakaoUser.getId(), "로그인 되었습니다");
    }

    private String getRandomDefaultProfileImage() {
        int randomNum = new Random().nextInt(7) + 1;
        return "defaultProfileImage/" + randomNum + ".png";
    }
}