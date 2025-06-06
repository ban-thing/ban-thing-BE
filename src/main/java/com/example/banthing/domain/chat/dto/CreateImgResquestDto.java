package com.example.banthing.domain.chat.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.banthing.domain.item.serialization.MultipartFileSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateImgResquestDto {
    
    

    @JsonSerialize(using = MultipartFileSerializer.class)
    private List<MultipartFile> images;

}
