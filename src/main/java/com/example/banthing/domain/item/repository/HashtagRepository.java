package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
}