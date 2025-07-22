package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.dto.UserReportRequestDto;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserReport;
import com.example.banthing.domain.user.repository.UserReportRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserReportService {

    private final UserReportRepository userReportRepository;
    private final UserService userService;

    @Transactional
    public void reportUser(Long reporterId, Long reportedUserId, UserReportRequestDto dto) {
        User reporter = userService.findById(reporterId);
        User reportedUser = userService.findById(reportedUserId);

        reportedUser.increaseReportCount(); // 신고당한 사람의 신고 수 증가
        userService.save(reportedUser);

        UserReport report = UserReport.builder()
                .reporter(reporter)
                .reportedUser(reportedUser)
                .reason(dto.getReason())
                .detailed_reason(dto.getDetailed_reason())
                .build();

        userReportRepository.save(report);
    }

}
