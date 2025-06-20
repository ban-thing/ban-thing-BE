package com.example.banthing.domain.item.service;

import com.example.banthing.admin.dto.AdminReportResponseDto;
import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;
import com.example.banthing.domain.item.repository.ItemReportRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.service.UserService;
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

    public void save(Long userId, Long itemId, ItemReportRequestDto itemReportRequestDto) {

        Item item = itemService.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("아이템이 존재하지 않습니다."));
        User user = userService.findById(userId);

        user.increaseReportCount();
        userService.save(user);

        ItemReport itemReport = ItemReport.builder()
                .item(item)
                .reporter(user) // 신고자
                .reportedUser(item.getSeller()) // 신고당한 유저 (판매자)
                .reason(itemReportRequestDto.getReason())
                .build();

        itemReportRepository.save(itemReport);
    }

    public Page<AdminReportResponseDto> findReportsByFilter(
            LocalDate startDate,
            LocalDate endDate,
            String reason,
            Pageable pageable
    ) {
        return itemReportRepository.findReportsByFilter(startDate, endDate, reason, pageable);
    }


}
