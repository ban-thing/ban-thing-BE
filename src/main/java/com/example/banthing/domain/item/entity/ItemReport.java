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
    @JoinColumn(name = "reporter_id", nullable = true)
    private User reporter;  // 신고한 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = true)
    private User reportedUser;  // 신고당한 유저

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String hiReason;  // 상위 신고 사유

    @Column(nullable = false)
    private String loReason;  // 하위 신고 이유

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ReportStatus reportStatus;

    @Builder
    public ItemReport(Item item, User reporter, User reportedUser, ReportStatus reportStatus, String hiReason, String loReason) {
        this.item = item;
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.hiReason = hiReason;
        this.loReason = loReason;
        this.reportStatus = reportStatus;
        this.userId = reporter.getId();
    }

}