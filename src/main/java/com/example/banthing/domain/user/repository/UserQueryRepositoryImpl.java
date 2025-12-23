package com.example.banthing.domain.user.repository;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.domain.item.entity.QItemReport;
import com.example.banthing.domain.user.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminUserResponseDto> findFilteredUsers(LocalDate startDate, LocalDate endDate,
                                                        String status, ReportFilterType reportFilterType,
                                                        String keyword, Pageable pageable) {
        QUser user = QUser.user;
        QUserReport userReport = QUserReport.userReport;

        BooleanBuilder builder = new BooleanBuilder();

        if (startDate != null && endDate != null) {
            builder.and(user.createdAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()));
        }

        if (status != null && !status.isBlank()) {
            builder.and(user.userStatus.eq(UserStatus.valueOf(status.toUpperCase())));
        }

        if (reportFilterType != null) {
            switch (reportFilterType) {
                case NO_REPORTS -> builder.and(user.reportCount.eq(0));
                case LESS_THAN_EQUAL_5 -> builder.and(user.reportCount.loe(5));
                case LESS_THAN_EQUAL_10 -> builder.and(user.reportCount.loe(10));
                case GREATER_THAN_10 -> builder.and(user.reportCount.gt(10));
            }
        }

        if (keyword != null && !keyword.isBlank()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();

            try {
                Long userId = Long.valueOf(keyword);
                keywordBuilder.or(user.id.eq(userId));
            } catch (NumberFormatException ignored) {}
            keywordBuilder.or(user.nickname.containsIgnoreCase(keyword));
            builder.and(keywordBuilder);
        }

        List<User> users = queryFactory
                .selectFrom(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        List<AdminUserResponseDto> dtos = users.stream().map(u -> {
            List<AdminUserResponseDto.ReportDetail> reportDetails = queryFactory
                    .select(Projections.constructor(AdminUserResponseDto.ReportDetail.class,
                            userReport.id,
                            userReport.createdAt,
                            userReport.reason,
                            Expressions.constant(""), // loReason 비워둠 (UserReport에는 없음)
                            userReport.reporter.id
                    ))
                    .from(userReport)
                    .where(userReport.reportedUser.id.eq(u.getId()))
                    .orderBy(userReport.createdAt.desc())
                    .fetch();

            return new AdminUserResponseDto(
                    u.getId(),
                    u.getNickname(),
                    u.getUserStatus().toString(),
                    u.getReportCount(),
                    u.getCreatedAt(),
                    reportDetails
            );
        }).toList();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(dtos, pageable, total);
    }
}