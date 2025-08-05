package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;

import java.util.List;

import com.example.banthing.domain.user.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long>, ReportQueryRepository {
    void deleteByItem(Item item);

    List<ItemReport> findAllByUserId(Long userId);

    @Modifying
    @Query("UPDATE ItemReport ir SET ir.reporter = null WHERE ir.reporter = :user")
    void nullifyReporter(@Param("user") User user);

    @Modifying
    @Query("UPDATE ItemReport ir SET ir.reportedUser = null WHERE ir.reportedUser = :user")
    void nullifyReportedUser(@Param("user") User user);

}
