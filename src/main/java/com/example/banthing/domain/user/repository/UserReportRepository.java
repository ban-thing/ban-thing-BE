package com.example.banthing.domain.user.repository;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserReport;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE UserReport ur SET ur.reportedUser = null WHERE ur.reportedUser = :user")
    void nullifyReportedUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("UPDATE UserReport ur SET ur.reporter = null WHERE ur.reporter = :user")
    void nullifyReporter(@Param("user") User user);
}