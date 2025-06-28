package com.example.banthing.domain.user.repository;

import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserDeletionReasonQueryRepository {
    Page<AdminUserDeletionResponseDto> findDeletionsByFilter(LocalDate startDate, LocalDate endDate, String reason, String keyword, Pageable pageable);
}

