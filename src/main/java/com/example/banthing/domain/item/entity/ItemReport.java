package com.example.banthing.domain.item.entity;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.global.common.Timestamped;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "item_reports")
public class ItemReport extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;  // 신고한 유저

    @Column(nullable = false)
    private String reason;  // 신고 이유

    @Column(nullable = false)
    private ReportStatus reportStatus;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ItemReport(Item item, User reporter, String reason, ReportStatus reportStatus) {
        this.item = item;
        this.reporter = reporter;
        this.reportStatus = reportStatus;
        this.reason = reason;
    }

}