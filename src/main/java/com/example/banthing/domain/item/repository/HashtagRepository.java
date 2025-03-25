package com.example.banthing.domain.item.repository;

import com.example.banthing.domain.item.entity.Hashtag;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemImg;
import com.example.banthing.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, Long> {
    List<Hashtag> findByItemId(Long itemId);
    @Modifying
    @Query("DELETE FROM Hashtag h WHERE h.item IN (SELECT i FROM Item i WHERE i.buyer = :user OR i.seller = :user)")
    void deleteByUser(@Param("user") User user);

    void deleteByItem(Item item);
}
