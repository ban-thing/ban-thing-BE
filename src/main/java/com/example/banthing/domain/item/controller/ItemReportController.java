package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.service.ItemReportService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import static com.example.banthing.global.common.ApiResponse.successResponse;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items/report")
public class ItemReportController {

    private final ItemReportService itemReportService;
    private final UserService userService;
    private final ItemService itemService;


    /*
     * 신고 등록
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<ApiResponse<?>> reportItem(
            @AuthenticationPrincipal String id,
            @PathVariable Long itemId,
            @RequestBody ItemReportRequestDto itemReportRequestDto) {
    
        itemReportService.save(Long.valueOf(id),itemId,itemReportRequestDto);
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("아이템 신고가 완료되었습니다."));
    }

    /*
     * 신고 삭제
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<?>> deleteReport(
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.deleteReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("삭제 처리"));
    }
    
    /*
     * 신고 어드민 삭제
     */
    @PostMapping("/adminDelete")
    public ResponseEntity<ApiResponse<?>> adminDeleteReport(
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminDeleteReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("삭제 처리"));
    }

    /*
     * 신고 어드민 무효
     */
    @PostMapping("/adminInvalid")
    public ResponseEntity<ApiResponse<?>> adminInvalidReport(
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminInvalidReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("무효 처리"));
    }

    /*
     * 신고 어드민 검토
     */
    @PostMapping("/adminCheck")
    public ResponseEntity<ApiResponse<?>> adminCheckReport(
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminCheckReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("검토"));
    }
    

}
