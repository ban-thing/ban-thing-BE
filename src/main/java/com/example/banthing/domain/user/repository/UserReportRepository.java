package com.example.banthing.domain.user.repository;

import com.example.banthing.domain.user.entity.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {
}