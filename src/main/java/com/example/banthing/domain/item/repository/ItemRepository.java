package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {

}
