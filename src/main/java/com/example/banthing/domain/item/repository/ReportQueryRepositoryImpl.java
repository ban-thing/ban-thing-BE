package com.example.banthing.domain.item.repository;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.CleaningDetailDto;
import com.example.banthing.domain.item.entity.*;
import com.example.banthing.domain.user.entity.QUser;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ReportQueryRepositoryImpl implements ReportQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AdminReportResponseDto> findReportsByFilter(LocalDate startDate, LocalDate endDate, String reason, Pageable pageable) {
        QItemReport report = QItemReport.itemReport;
        QItem item = QItem.item;
        QUser seller = QUser.user;
        QHashtag hashtag = new QHashtag("hashtag");
        QCleaningDetail detail = QCleaningDetail.cleaningDetail;

        BooleanBuilder builder = new BooleanBuilder();
        if (startDate != null && endDate != null) {
            builder.and(report.createdAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()));
        }
        if (reason != null && !reason.isBlank()) {
            builder.and(report.loReason.containsIgnoreCase(reason));
        }

        List<ItemReport> reports = queryFactory
                .selectFrom(report)
                .join(report.item, item).fetchJoin()
                .join(item.seller, seller).fetchJoin()
                .leftJoin(item.cleaningDetail, detail).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(report.createdAt.desc())
                .fetch();

        List<AdminReportResponseDto> dtos = reports.stream().map(r -> {
            Item i = r.getItem();

            List<String> hashtags = queryFactory
                    .select(hashtag.hashtag)
                    .from(hashtag)
                    .where(hashtag.item.id.eq(i.getId()))
                    .fetch();

            CleaningDetail d = i.getCleaningDetail();
            CleaningDetailDto cleaningDto = (d == null) ? null :
                    new CleaningDetailDto(
                            d.getPollution(),
                            d.getTimeUsed(),
                            d.getPurchasedDate(),
                            d.getCleaned(),
                            d.getExpire()
                    );

            return new AdminReportResponseDto(
                    r.getId(),
                    i.getTitle(),
                    r.getHiReason(),
                    r.getLoReason(),
                    r.getCreatedAt(),
                    r.getReporter().getId(),
                    r.getReportedUser().getId(),
                    r.getReportedUser().getUserStatus().toString(),
                    // 상세 항목
                    i.getContent(),
                    i.getCreatedAt(),
                    i.getSeller().getNickname(),
                    hashtags,
                    cleaningDto.getPollution(),
                    cleaningDto.getTimeUsed(),
                    cleaningDto.getPurchasedDate(),
                    cleaningDto.getCleaned()
            );
        }).toList();

        Long total = queryFactory
                .select(report.count())
                .from(report)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(dtos, pageable, total);
    }
}


