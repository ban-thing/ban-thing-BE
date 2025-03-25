package com.example.banthing.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_deletion_reasons")
public class UserDeletionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String reason;

    public UserDeletionReason(String reason) {
        this.reason = reason;
    }
}
