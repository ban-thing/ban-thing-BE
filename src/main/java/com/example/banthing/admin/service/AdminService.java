package com.example.banthing.admin.service;

import com.example.banthing.admin.dto.AdminLoginRequestDto;
import com.example.banthing.admin.dto.AdminLoginResponseDto;
import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.item.service.ItemReportService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserStatus;
import com.example.banthing.domain.user.service.UserDeletionReasonService;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.security.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ItemReportService itemReportService;
    private final UserDeletionReasonService userDeletionReasonService;

    public Page<AdminUserResponseDto> getFilteredAccounts(
            LocalDate startDate,
            LocalDate endDate,
            String status,
            ReportFilterType reportFilterType,
            Pageable pageable
    ) {
        return userService.findFilteredUsers(startDate, endDate, status, reportFilterType, pageable);
    }

    public String login(String username, String password) {


        if(!"banthing-admin".equals(username))
        {
            log.info(username);
            throw new IllegalArgumentException("아이디가 일치하지 않습니다.");
        }
        else if(!"banthing-admin123".equals(password))
        {
            log.info(password);
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        else
        {
            log.info(username);
            log.info(password);
            return jwtUtil.createToken(username);
            
        }
    }
       
    public Page<AdminReportResponseDto> getFilteredReports(
            LocalDate startDate,
            LocalDate endDate,
            String hiReason,
            String status,
            Pageable pageable,
            String keyword
    ) {
        return itemReportService.findReportsByFilter(startDate, endDate, hiReason, status, pageable, keyword);
    }


    public Page<AdminUserDeletionResponseDto> getDeletions(LocalDate startDate, LocalDate endDate, String reason, String keyword, Pageable pageable) {
        return userDeletionReasonService.findDeletionsByFilter(startDate, endDate, reason, keyword, pageable);
    }

    @Transactional
    public void suspendUser(Long userId) {
        User user = userService.findById(userId);
        user.suspend();
        userService.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {
        User user = userService.findById(userId);
        user.activate();
        userService.save(user);
    }

}