package com.example.banthing.domain.wishlist.controller;

import com.example.banthing.domain.wishlist.dto.WishlistResponseDTO;
import com.example.banthing.domain.wishlist.service.WishlistService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@RestController
@RequiredArgsConstructor
//@RequestMapping("")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     *
     * 찜 하기
     *
     **/
    @PostMapping("/items/{itemId}/wishlist")
    public ResponseEntity<?> requestWishlist(@AuthenticationPrincipal String userId, @PathVariable("itemId") Long itemId){
        wishlistService.requestWishlist(Long.valueOf(userId),itemId);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("찜 성공"));
    }

    /**
     *
     * 찜 삭제
     *
     */
    @DeleteMapping("/items/{itemId}/wishlist")
    public ResponseEntity<ApiResponse<?>> deleteWishlist(@AuthenticationPrincipal String userId, @PathVariable Long itemId) {
        wishlistService.deleteWishlist(Long.valueOf(userId), itemId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("찜 삭제 성공"));
    }

    /**
     *
     * 찜 삭제 (백엔드용)
     *
     */
    @DeleteMapping("/items/{itemId}/wishlistAdmin")
    public ResponseEntity<ApiResponse<?>> deleteAdminWishlist(@PathVariable Long itemId) {
        wishlistService.deleteAdminWishlist(itemId);
        return ResponseEntity.ok(ApiResponse.successWithMessage("찜 삭제 성공"));
    }

    /**
     *
     * 찜 목록 조회
     *
     */
    @GetMapping("/my/wishlist")
    public ResponseEntity<ApiResponse<List<WishlistResponseDTO>>> getWishlist(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok().body(successResponse(wishlistService.findUserWishlist(Long.valueOf(userId))));
    }
}
