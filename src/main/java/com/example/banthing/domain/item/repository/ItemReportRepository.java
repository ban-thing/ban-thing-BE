package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemReportRepository extends JpaRepository<ItemReport, Long>, ReportQueryRepository {
    void deleteByItem(Item item);

    List<ItemReport> findAllByUserId(Long userId);
}
