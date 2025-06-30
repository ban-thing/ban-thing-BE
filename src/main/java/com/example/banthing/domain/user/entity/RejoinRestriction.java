package com.example.banthing.domain.user.entity;

import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "rejoin_restrictions")
public class RejoinRestriction extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long socialId;

    @Column(nullable = false)
    private Long userId;

}
