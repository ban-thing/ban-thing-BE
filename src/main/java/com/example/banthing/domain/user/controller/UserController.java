package com.example.banthing.domain.user.controller;

import com.example.banthing.domain.user.dto.ProfileResponseDto;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.example.banthing.global.common.ApiResponse.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> findMyProfile(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findById(Long.valueOf(userId))));
    }

}
