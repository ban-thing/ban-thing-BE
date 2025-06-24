package com.example.banthing.domain.user.controller;

import com.example.banthing.domain.user.dto.*;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
//@CrossOrigin(origins = "https://banthing.net")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponseDto>> findMyProfile(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findMyProfile(Long.valueOf(userId))));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UpdateProfileResponseDto>> updateMyProfile(
            @AuthenticationPrincipal String userId,
            @RequestPart(required = false, name = "profileImg") MultipartFile file,
            @RequestPart(required = false, name = "nickname") String nickname)  throws IOException {
        return ResponseEntity.ok().body(successResponse(userService.updateMyProfile(Long.valueOf(userId), file, nickname)));
    }

    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<PurchaseResponseDto>>> findMyPurchases(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findPurchasesById(Long.valueOf(userId))));
    }

    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<List<SalesResponseDto>>> findMySales(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(userService.findSalesById(Long.valueOf(userId))));
    }

    @PatchMapping("/address")
    public ResponseEntity<ApiResponse<UpdateAddressResponseDto>> updateMyAddress(
            @AuthenticationPrincipal String userId,
            @RequestBody UpdateAddressRequestDto request) {
        return ResponseEntity.ok().body(successResponse(userService.updateAddress(Long.valueOf(userId), request)));
    }

    /**
     *
     * 회원 탈퇴
     *
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<?>> deleteUser(@AuthenticationPrincipal String userId,
                                                     @RequestParam UserDeletionRequestDto request) {
        userService.deleteUser(Long.valueOf(userId), request.getReason());
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("회원 탈퇴가 완료되었습니다."));
    }

    /**
     *
     * 회원 강제 탈퇴
     *
     */
    @PostMapping("/forcedDelete")
    public ResponseEntity<ApiResponse<?>> forcedDeleteUser(@RequestParam String userId,
                                                     @RequestParam String reason) {
        userService.deleteUser(Long.valueOf(userId), reason);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("회원 탈퇴가 완료되었습니다."));
    }
}