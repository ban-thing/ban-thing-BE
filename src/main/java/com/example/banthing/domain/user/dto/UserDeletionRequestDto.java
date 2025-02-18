package com.example.banthing.domain.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDeletionRequestDto {
    private String reason;
}
