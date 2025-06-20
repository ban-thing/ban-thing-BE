package com.example.banthing.admin.service;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.item.service.ItemReportService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.service.UserDeletionReasonService;
import com.example.banthing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserService userService;
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

    public Page<AdminReportResponseDto> getFilteredReports(
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            Pageable pageable
    ) {
        return itemReportService.findReportsByFilter(startDate, endDate, reason, pageable);
    }


    public Page<AdminUserDeletionResponseDto> getDeletions(LocalDate startDate, LocalDate endDate, String reason, Pageable pageable) {
        return userDeletionReasonService.findDeletionsByFilter(startDate, endDate, reason, pageable);
    }

}
