package com.example.banthing.domain.user.controller;

import com.example.banthing.domain.user.dto.UserReportRequestDto;
import com.example.banthing.domain.user.service.UserReportService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-report")
public class UserReportController {

    private final UserReportService userReportService;

    /**
     *
     * 작성자 신고
     *
     */
    @PostMapping("/{reportedUserId}")
    public ResponseEntity<ApiResponse<?>> reportUser(
            @AuthenticationPrincipal String userId,  // 로그인한 사용자 ID (신고자)
            @RequestParam Long reportedUserId,       // 신고당한 사용자 ID
            @RequestParam String reason,
            @RequestParam String detailed_reason
    ) {
        userReportService.reportUser(Long.valueOf(userId), reportedUserId, reason, detailed_reason);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("회원 신고가 완료되었습니다."));
    }
}
