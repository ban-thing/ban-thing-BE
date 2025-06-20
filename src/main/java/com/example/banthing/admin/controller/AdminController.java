package com.example.banthing.admin.controller;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.admin.service.AdminService;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static com.example.banthing.global.common.ApiResponse.successResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserResponseDto> result = adminService.getFilteredAccounts(startDate, endDate, status, reportFilterType, pageable);
        return ResponseEntity.ok().body(successResponse(result));
    }

    /**
     *
     * 신고내역
     *
     */
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Page<AdminReportResponseDto>>> getReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String reason,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminReportResponseDto> result = adminService.getFilteredReports(startDate, endDate, reason, pageable);
        return ResponseEntity.ok().body(successResponse(result));
    }

    /**
     *
     * 탈퇴내역
     *
     */
    @GetMapping("/deletions")
    public ResponseEntity<ApiResponse<Page<AdminUserDeletionResponseDto>>> getUserDeletions(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "") String reason,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AdminUserDeletionResponseDto> result = adminService.getDeletions(startDate, endDate, reason, pageable);
        return ResponseEntity.ok().body(successResponse(result));
    }
}
