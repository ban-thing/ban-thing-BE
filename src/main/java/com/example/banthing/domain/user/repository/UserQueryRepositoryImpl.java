package com.example.banthing.domain.user.repository;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.admin.dto.QAdminUserResponseDto;
import com.example.banthing.domain.user.entity.QUser;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.entity.UserStatus;
import com.querydsl.core.BooleanBuilder;
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
                                                        String status, ReportFilterType reportFilterType, Pageable pageable) {
        QUser user = QUser.user;

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

        List<AdminUserResponseDto> results = queryFactory
                .select(new QAdminUserResponseDto(
                        user.id,
                        user.nickname,
                        user.userStatus.stringValue(),
                        user.reportCount,
                        user.createdAt
                ))
                .from(user)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}