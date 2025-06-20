package com.example.banthing.admin.controller;

import com.example.banthing.admin.dto.AdminLoginRequestDto;
import com.example.banthing.admin.dto.AdminLoginResponseDto;
import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.admin.service.AdminService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.banthing.global.common.ApiResponse.successResponse;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

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
        return ResponseEntity.ok().body(successResponse( adminService.getFilteredAccounts(startDate, endDate, status, reportFilterType, pageable)));
    }

    @GetMapping("/login")
    public ResponseEntity<ApiResponse<AdminLoginResponseDto>> adminLogin(
        @RequestBody AdminLoginRequestDto request
    ) {
        String response = adminService.login(request.getUsername(), request.getPassword());
        log.info(response);
        return ResponseEntity.ok().body(successResponse(new AdminLoginResponseDto(response)));
    }


}
