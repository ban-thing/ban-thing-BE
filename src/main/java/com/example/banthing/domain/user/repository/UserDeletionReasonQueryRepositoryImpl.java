package com.example.banthing.domain.user.repository;

import com.example.banthing.admin.dto.AdminUserDeletionResponseDto;
import com.example.banthing.domain.user.entity.QUserDeletionReason;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class UserDeletionReasonQueryRepositoryImpl implements UserDeletionReasonQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminUserDeletionResponseDto> findDeletionsByFilter(LocalDate startDate, LocalDate endDate, String reason, String keyword, Pageable pageable) {
        QUserDeletionReason qDeletion = QUserDeletionReason.userDeletionReason;

        BooleanExpression dateCondition = qDeletion.deletedAt.between(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay()
        );

        BooleanExpression reasonCondition = reason != null && !reason.isBlank()
                ? qDeletion.reason.containsIgnoreCase(reason)
                : null;

        BooleanBuilder builder = new BooleanBuilder();        
        if (keyword != null && keyword.isBlank()) {


                try {
                        Long userId = Long.valueOf(keyword);
                        builder.and(qDeletion.userId.eq(userId));
                } catch (NumberFormatException ignored) {}
                builder.and(qDeletion.memo.containsIgnoreCase(keyword));
        }

        List<AdminUserDeletionResponseDto> content = queryFactory
                .select(Projections.constructor(AdminUserDeletionResponseDto.class,
                        qDeletion.userId,
                        qDeletion.joinedAt,
                        qDeletion.deletedAt,
                        qDeletion.lastLoginAt,
                        qDeletion.reason,
                        qDeletion.memo,
                        qDeletion.isRejoinRestricted
                ))
                .from(qDeletion)
                .where(dateCondition, reasonCondition, builder)
                .orderBy(qDeletion.deletedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qDeletion.count())
                .from(qDeletion)
                .where(dateCondition, reasonCondition)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}

