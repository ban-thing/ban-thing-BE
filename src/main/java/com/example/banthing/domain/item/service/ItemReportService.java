package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;
import com.example.banthing.domain.item.repository.ItemReportRepository;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        ItemReport itemReport = itemReportRepository.save(ItemReport.builder()
                .item(item)
                .reporter(user)
                .reason(itemReportRequestDto.getReason())
                .build());

        itemReportRepository.save(itemReport);
    }

}
