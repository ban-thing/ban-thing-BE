package com.example.banthing.domain.item.repository;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReportQueryRepository {
    Page<AdminReportResponseDto> findReportsByFilter(LocalDate startDate, LocalDate endDate, String hiReason, String loReason, Pageable pageable, String keyword);

}

