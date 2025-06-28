package com.example.banthing.domain.user.service;

import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserDeletionReason;
import com.example.banthing.domain.user.repository.UserDeletionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserDeletionReasonService {

    private final UserDeletionReasonRepository userDeletionReasonRepository;

    public void save(UserDeletionReason deletionReason) {
        userDeletionReasonRepository.save(deletionReason);
    }

    public Page<AdminUserDeletionResponseDto> findDeletionsByFilter(LocalDate startDate, LocalDate endDate, String reason, String keyword, Pageable pageable) {
        return userDeletionReasonRepository.findDeletionsByFilter(startDate, endDate, reason, keyword, pageable);
    }
}