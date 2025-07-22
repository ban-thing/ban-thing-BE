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


        BooleanBuilder builder = new BooleanBuilder();

        if(startDate != null && endDate != null) {
                builder.and(qDeletion.deletedAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()));
        }

        if(reason != null && !reason.isBlank()) {
                builder.and(qDeletion.reason.containsIgnoreCase(reason));
        }

        if (keyword != null && !keyword.isBlank()) {
                BooleanBuilder keywordBuilder = new BooleanBuilder();
                
                try{
                    Long userId = Long.valueOf(keyword);
                    keywordBuilder.or(qDeletion.userId.eq(userId));
                } catch (NumberFormatException ignored) {}
    
                keywordBuilder.or(qDeletion.memo.containsIgnoreCase(keyword));
                builder.and(keywordBuilder);
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
                .where(builder)
                .orderBy(qDeletion.deletedAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(qDeletion.count())
                .from(qDeletion)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}

