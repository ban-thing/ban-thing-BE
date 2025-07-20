package com.example.banthing.admin.controller;

import com.example.banthing.admin.dto.AdminLoginRequestDto;
import com.example.banthing.admin.dto.AdminLoginResponseDto;
import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.admin.service.AdminService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.RejoinRestriction;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.service.UserDeletionReasonService;
import com.example.banthing.domain.user.service.RejoinRestrictionService;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;
import com.example.banthing.global.security.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserDeletionReason;
import com.example.banthing.domain.user.entity.UserStatus;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.banthing.global.common.ApiResponse.successResponse;
import static com.example.banthing.global.common.ApiResponse.successWithMessage;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.banthing.global.common.ApiResponse.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;
    private final RejoinRestrictionService rejoinRestrictionService;
    public static Logger logger = LoggerFactory.getLogger("어드민 관련 로그");
    public final UserDeletionReasonService userDeletionReasonService;


    /**
     *
     * 계정관리
     *
     */
    @GetMapping("/account")
    public ResponseEntity<ApiResponse<Page<AdminUserResponseDto>>> getAccounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) ReportFilterType reportFilterType,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponseDto> result = adminService.getFilteredAccounts(startDate, endDate, status, reportFilterType, keyword, pageable);
        return ResponseEntity.ok().body(successResponse(result));
    }



    /**
     *
     * 신고내역 조회
     *
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<AdminReportResponseDto>>> getReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String hiReason,
            @RequestParam(required = false, defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String keyword
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminReportResponseDto> result = adminService.getFilteredReports(startDate, endDate, hiReason, status, pageable, keyword);
        return ResponseEntity.ok().body(successResponse(result));
    }

    /**
     *
     * 탈퇴내역 조회
     *
     */
    @GetMapping("/deletions")
    public ResponseEntity<ApiResponse<Page<AdminUserDeletionResponseDto>>> getUserDeletions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String reason,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserDeletionResponseDto> result = adminService.getDeletions(startDate, endDate, reason, keyword, pageable);
        return ResponseEntity.ok().body(successResponse(result));
    }

    /**
     * 
     * 어드민 로그인
     * 
     **/
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponseDto>> adminLogin(
        @RequestParam String username,
        @RequestParam String password,
        HttpServletResponse httpResponse
    ) {
        String token = adminService.login(username, password);
        logger.info(token);
        httpResponse.addHeader(JwtUtil.AUTHORIZATION_HEADER, token);
        return ResponseEntity.ok().body(successResponse(new AdminLoginResponseDto(token)));
    }



    /**
     *
     * 재가입 제한
     *
     */
    @PostMapping("/rejoin-restriction")
    public ResponseEntity<ApiResponse<?>> addRestriction(@RequestParam Long userId) {
        try {
            rejoinRestrictionService.addRestrictionByUserId(userId);
            return ResponseEntity.ok(successWithNoContent());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     *
     * 계정 정지 (SUSPENDED)
     *
     **/
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<ApiResponse<?>> suspendUser(@PathVariable Long userId) {
        try {
            adminService.suspendUser(userId);
            return ResponseEntity.ok(ApiResponse.successWithMessage("계정이 정지되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     *
     * 계정 활성화 (ACTIVE)
     *
     **/
    @PostMapping("/{userId}/activate")
    public ResponseEntity<ApiResponse<?>> activateUser(@PathVariable Long userId) {
        try {
            adminService.activateUser(userId);
            return ResponseEntity.ok(ApiResponse.successWithMessage("계정이 활성화되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
