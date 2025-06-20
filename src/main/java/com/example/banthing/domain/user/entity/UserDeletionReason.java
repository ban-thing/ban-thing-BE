package com.example.banthing.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_deletion_reason")
public class UserDeletionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long socialId;

    private String email;

    private LocalDateTime joinedAt;

    private LocalDateTime deletedAt;

    private LocalDateTime lastLoginAt;

    @Column(length = 500)
    private String reason;

    @Column(length = 1000)
    private String memo;

    private boolean isRejoinRestricted;
}

