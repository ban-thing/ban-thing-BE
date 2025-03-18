package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    List<ItemImg> findByItemId(Long itemId);

    void deleteByItem(Item item);
}
