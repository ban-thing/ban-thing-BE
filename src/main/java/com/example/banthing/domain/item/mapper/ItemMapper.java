package com.example.banthing.domain.item.mapper;

import com.example.banthing.domain.item.dto.ItemSearchRequestDto;
import com.example.banthing.domain.item.dto.ItemSearchResponseDto;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Mapper
public interface ItemMapper {

    /**
     * Retrieves a list of items between specified indices for pagination.
     * @param low
     * @param high
     * @param address
     * @return a list of ItemResponseDto representing the items
     */
    // * 대신 ItemResonseDto element로 변경
    @Select("""
        SELECT i.id AS id,
               i.updated_at AS updated_at,
               i.address AS address,
               i.price AS price,
               i.title AS title,
               i.type AS type,
               GROUP_CONCAT(h.hashtag) AS hashtags
        FROM items i
        LEFT JOIN hashtags h ON i.id = h.item_id
        WHERE i.title LIKE CONCAT('%', #{keyword}, '%')
        GROUP BY i.id, i.updated_at, i.address, i.price, i.title, i.type
        """)
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "updatedAt", property = "updated_at"),
        @Result(column = "address", property = "address"),
        @Result(column = "price", property = "price"),
        @Result(column = "title", property = "title"),
        @Result(column = "type", property = "type"),
        @Result(column = "hashtags", property = "hashtag", javaType = List.class, typeHandler = MyBatisListHandler.class)
    })
    List<ItemSearchResponseDto> listItems(@Param("keyword") String keyword, @Param("low") int filter_low, @Param("high") int filter_high, @Param("address") String address);


    @Query("""
        SELECT i 
        FROM items i
        LEFT JOIN FETCH i.hashtags h
        WHERE title LIKE CONCAT('%', :keyword, '%')
        AND i.price BETWEEN :filter_low AND :filter_high
        """)
    @Results({
        @Result(column = "id", property = "id"),
        @Result(column = "updatedAt", property = "updated_at"),
        @Result(column = "address", property = "address"),
        @Result(column = "price", property = "price"),
        @Result(column = "title", property = "title"),
        @Result(column = "type", property = "type"),
        @Result(column = "hashtags", property = "hashtag") //, javaType = List.class, typeHandler = MyBatisListHandler.class)
    })
    List<ItemSearchResponseDto> listFilteredItems(@Param("keyword") String keyword, @Param("low") int filter_low, @Param("high") int filter_high, @Param("address") String address);
}
