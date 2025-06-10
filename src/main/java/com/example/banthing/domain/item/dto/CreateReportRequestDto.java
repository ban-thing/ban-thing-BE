package com.example.banthing.admin.dto;

import java.io.ObjectInputFilter.Status;
import java.security.cert.CertPathValidatorException.Reason;

import com.example.banthing.domain.user.entity.User;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReportRequestDto {
    private Long userId;
    private Long itemId;
    private String title;
    private String content;
    
    private String reason; // 상위 신고 사유
    private String sec_reason; // 하위 신고 사유
    
    private String status; // 상태
    
    private Long reporter; // 신고자 ID
    private Long reportee; // 피신고자 ID

}
