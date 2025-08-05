package com.example.banthing.domain.item.service;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;
import com.example.banthing.domain.item.entity.ItemStatus;
import com.example.banthing.domain.item.entity.ReportStatus;
import com.example.banthing.domain.item.repository.ItemReportRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ItemReportService {

    private final ItemReportRepository itemReportRepository;
    private final ItemService itemService;
    private final UserService userService;

    public void save(
        Long userId, 
        Long itemId, 
        ItemReportRequestDto itemReportRequestDto
    ) {

        Item item = itemService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템이 존재하지 않습니다."));
        User user = userService.findById(userId);
        userService.save(user);

        ItemReport itemReport = ItemReport.builder()
                .item(item)
                .reporter(user) // 신고자
                .reportedUser(item.getSeller()) // 신고당한 유저 (판매자)
                .hiReason(itemReportRequestDto.getHiReason())
                .loReason(itemReportRequestDto.getLoReason())
                .reportStatus(ReportStatus.미처리) // 초기 생성시 미처리로 초기화
                .build();

        itemReportRepository.save(itemReport);
    }
    /*
     * 신고 삭제
     */
    @Transactional
    public void deleteReport(
            Long reportId
    ) {
        ItemReport report = itemReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));

        itemReportRepository.delete(report);
    }

    /*
     * 어드민 신고 완전삭제 (백엔드용)
     */
    @Transactional
    public void adminAbsoluteDeleteReport(
            Long reportId
    ) {
        ItemReport report = itemReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));
        
        itemReportRepository.delete(report);

    }

    /*
     * 어드민 신고 삭제
     */
    @Transactional
    public void adminDeleteReport(
            Long reportId
    ) {
        ItemReport report = itemReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));
        
        Item item = report.getItem();

        item.setStatus(ItemStatus.삭제); // 삭제 대신 이력을 남김

        report.setReportStatus(ReportStatus.처리완료);
    }

    /*
     * 어드민 신고 무효
     */
    @Transactional
    public void adminInvalidReport(
        Long reportId
    ) {
        ItemReport report = itemReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));

        report.setReportStatus(ReportStatus.무효처리);

    }

    /*
     * 어드민 신고 검토
     */
    @Transactional
    public void adminCheckReport(
        Long reportId
    ) {        
        ItemReport report = itemReportRepository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));

        report.setReportStatus(ReportStatus.처리중);

    }

    public Page<AdminReportResponseDto> findReportsByFilter(
            LocalDate startDate,
            LocalDate endDate,
            String hiReason,
            String status,
            Pageable pageable,
            String keyword
    ) {
        return itemReportRepository.findReportsByFilter(startDate, endDate, hiReason, status, pageable, keyword);
    }

}
