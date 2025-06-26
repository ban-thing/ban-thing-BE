package com.example.banthing.domain.item.repository;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.admin.dto.CleaningDetailDto;
import com.example.banthing.domain.item.entity.*;
import com.example.banthing.domain.user.entity.QUser;
import com.example.banthing.global.s3.S3Service;
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

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ReportQueryRepositoryImpl implements ReportQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final S3Service s3Service;

    @Override
    public Page<AdminReportResponseDto> findReportsByFilter(LocalDate startDate, LocalDate endDate, String hiReason, String loReason, String status, Pageable pageable, String keyword) {
        QItemReport report = QItemReport.itemReport;
        QItem item = QItem.item;
        QUser seller = QUser.user;
        QHashtag hashtag = new QHashtag("hashtag");
        QCleaningDetail detail = QCleaningDetail.cleaningDetail;
        

        BooleanBuilder builder = new BooleanBuilder();
        if (startDate != null && endDate != null) {
            builder.and(report.createdAt.between(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay()));
        }
        if (hiReason != null && !hiReason.isBlank()) {
            builder.and(report.hiReason.containsIgnoreCase(hiReason));
        }

        if (loReason != null && !loReason.isBlank()) {
            builder.and(report.loReason.containsIgnoreCase(loReason));
        }

        if (status != null && !status.isBlank()) {
            if(status.equals("미처리")) builder.and(report.reportStatus.eq(ReportStatus.미처리));
            else if(status.equals("처리완료")) builder.and(report.reportStatus.eq(ReportStatus.처리완료));
            else if(status.equals("무효처리")) builder.and(report.reportStatus.eq(ReportStatus.무효처리));
            else if(status.equals("처리중")) builder.and(report.reportStatus.eq(ReportStatus.처리중));
            
        }

        if (keyword != null && !keyword.isBlank()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            
            try{
                Long userId = Long.valueOf(keyword);
                keywordBuilder.or(report.reporter.id.eq(userId));
                keywordBuilder.or(report.reportedUser.id.eq(userId));
            } catch (NumberFormatException ignored) {}

            keywordBuilder.or(report.item.title.containsIgnoreCase(keyword));
            builder.and(keywordBuilder);
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

            List<String> base64Images = i.getImages().stream()
                .filter(Objects::nonNull)
                .map((ItemImg img) -> {
                    try {
                        return s3Service.encodeImageToBase64(img.getImgUrl(), "itemImage");
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to encode item image to Base64", e);
                    }
                })
                .collect(Collectors.toList());

            return new AdminReportResponseDto(
                    r.getId(),
                    i.getTitle(),
                    r.getHiReason(),
                    r.getLoReason(),
                    r.getCreatedAt(),
                    r.getReporter().getId(),
                    r.getReportedUser().getId(),
                    r.getReportStatus().toString(),
                    // 상세 항목
                    i.getContent(),
                    i.getCreatedAt(),
                    i.getSeller().getNickname(),
                    hashtags,
                    base64Images,
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


