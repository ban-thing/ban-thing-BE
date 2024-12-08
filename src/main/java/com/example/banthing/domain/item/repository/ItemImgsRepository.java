package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.ItemImgs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgsRepository extends JpaRepository<ItemImgs, Long> {
    List<ItemImgs> findByItemId(Long itemId);
}
