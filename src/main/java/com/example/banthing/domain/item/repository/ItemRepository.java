package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    void deleteByBuyerOrSeller(User buyer, User seller);

    List<Item> findByBuyerOrSeller(User buyer, User seller);
}
