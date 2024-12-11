package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByItemId(Long itemId);
}
