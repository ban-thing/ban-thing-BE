package com.example.banthing.domain.item.controller;

import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.dto.ItemReportDetailResponseDto;
import com.example.banthing.domain.item.dto.ItemReportRequestDto;
import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.entity.ItemReport;
import com.example.banthing.domain.item.repository.ItemReportRepository;
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
    private final ItemReportRepository itemReportRepository;
    private final ItemService itemService;

    /*
     * 신고 등록
     */
    @PostMapping("/{itemId}")
    public ResponseEntity<ApiResponse<?>> reportItem(
            @RequestParam String id,
            @PathVariable Long itemId,
            @RequestParam String reason) {
    


        itemReportService.save(Long.valueOf(id),itemId,new ItemReportRequestDto(reason));
        return ResponseEntity.ok().body(ApiResponse.successWithMessage("아이템 신고가 완료되었습니다."));
    }

    /*
     * 신고 어드민 삭제
     */
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<?>> adminDeleteReport(
        @RequestParam(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminDeleteReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("삭제 처리"));
    }

    /*
     * 신고 어드민 무효
     */
    @PostMapping("/invalid")
    public ResponseEntity<ApiResponse<?>> adminInvalidReport(
        @RequestParam(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminInvalidReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("무효 처리"));
    }

    /*
     * 신고 어드민 검토
     */
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<?>> adminCheckReport(
        @RequestParam(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < reportIdList.size() ; i++) {
            itemReportService.adminCheckReport(reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("검토"));
    }
    

    /*
     * 신고 이력 상세보기 
     */
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<?>> reportDetail(
        @RequestParam(required = true) Long userId
    ) {

        List<ItemReport> itemReportList = itemReportRepository.findAllByUserId(userId);

        return ResponseEntity.ok().body(successResponse(itemReportList));
    }

    /*
     * 신고한 글 상세보기 
     */
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> reportInfo(
        @RequestParam(required = true) Long reportId
    ) {
        
        ItemReport itemReport = itemReportRepository.findById(reportId)
        .orElseThrow(() -> new IllegalArgumentException("신고글이 존재하지 않습니다."));

        Item item = itemReport.getItem();
        CleaningDetail cleaningDetail = item.getCleaningDetail();

        return ResponseEntity.ok().body(successResponse(new ItemReportDetailResponseDto(itemReport, item, cleaningDetail)));

    }

}
