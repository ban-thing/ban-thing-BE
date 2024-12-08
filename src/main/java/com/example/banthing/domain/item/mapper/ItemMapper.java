package com.example.banthing.domain.item.mapper;

import com.example.banthing.domain.item.dto.ItemResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

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
    @Select("SELECT * FROM items WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<ItemResponseDto> listItems(@Param("keyword") String keyword, @Param("low") int filter_low, @Param("high") int filter_high, @Param("address") String address);

}