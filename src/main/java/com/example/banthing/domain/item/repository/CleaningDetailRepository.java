package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.CleaningDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface CleaningDetailRepository extends JpaRepository<CleaningDetail, Long> {
}
