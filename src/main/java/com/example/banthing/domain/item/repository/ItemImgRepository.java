package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    List<ItemImg> findByItemId(Long itemId);
}
