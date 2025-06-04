package com.example.banthing.domain.user.repository;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.user.entity.ReportFilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserQueryRepository {
    Page<AdminUserResponseDto> findFilteredUsers(LocalDate startDate, LocalDate endDate,
                                                 String status, ReportFilterType reportFilterType, Pageable pageable);
}

