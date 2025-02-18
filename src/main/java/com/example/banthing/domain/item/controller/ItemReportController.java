package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.service.ItemReportService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items/report")
public class ItemReportController {

    private final ItemReportService itemReportService;

    @PostMapping("/{itemId}")
    public ResponseEntity<ApiResponse<?>> reportItem(
            @AuthenticationPrincipal String id,
            @PathVariable Long itemId,
            @RequestBody ItemReportRequestDto itemReportRequestDto) {
    
        itemReportService.save(Long.valueOf(id),itemId,itemReportRequestDto);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("아이템 신고가 완료되었습니다."));
    }
}
