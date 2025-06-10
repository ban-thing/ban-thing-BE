package com.example.banthing.admin.service;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.service.UserService;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserService userService;

    public Page<AdminUserResponseDto> getFilteredAccounts(
            LocalDate startDate,
            LocalDate endDate,
            String status,
            ReportFilterType reportFilterType,
            Pageable pageable
    ) {
        return userService.findFilteredUsers(startDate, endDate, status, reportFilterType, pageable);
    }

}
