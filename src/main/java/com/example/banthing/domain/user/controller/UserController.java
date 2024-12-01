package com.example.banthing.domain.user.controller;

import com.example.banthing.domain.user.dto.ProfileResponseDto;
import com.example.banthing.domain.user.dto.PurchaseResponseDto;
import com.example.banthing.domain.user.dto.SalesResponseDto;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> findMyProfile(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findMyProfile(Long.valueOf(userId))));
    }

    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> findMyPurchases(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findPurchasesById(Long.valueOf(userId))));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<SalesResponseDto>>> findMySales(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findSalesById(Long.valueOf(userId))));
    }
}
