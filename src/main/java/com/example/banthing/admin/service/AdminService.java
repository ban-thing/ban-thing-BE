package com.example.banthing.admin.service;

import com.example.banthing.admin.dto.AdminLoginRequestDto;
import com.example.banthing.admin.dto.AdminLoginResponseDto;
import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.security.JwtUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public Page<AdminUserResponseDto> getFilteredAccounts(
            LocalDate startDate,
            LocalDate endDate,
            String status,
            ReportFilterType reportFilterType,
            Pageable pageable
    ) {
        return userService.findFilteredUsers(startDate, endDate, status, reportFilterType, pageable);
    }

    public AdminLoginResponseDto login(AdminLoginRequestDto request) {

        if(request.getUsername() != "banthing-admin")
        {
            throw new IllegalArgumentException(request.getUsername());
        }
        else if(request.getPassword() != "banthing-admin123")
        {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        else
        {
            String token = jwtUtil.createToken(String.valueOf(request.getUsername()));
            return new AdminLoginResponseDto(token);
        }
        
    }



}
