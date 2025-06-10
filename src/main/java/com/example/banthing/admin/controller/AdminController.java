package com.example.banthing.admin.controller;

import com.example.banthing.admin.dto.AdminUserResponseDto;
import com.example.banthing.admin.dto.CreateReportRequestDto;
import com.example.banthing.admin.service.AdminService;
import com.example.banthing.domain.item.dto.CreateItemRequestDto;
import com.example.banthing.domain.item.dto.ItemDto;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.entity.ReportFilterType;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.service.UserService;
import com.example.banthing.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.banthing.global.common.ApiResponse.successResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {


    private final AdminService adminService;
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<Page<AdminUserResponseDto>>> getAccounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) ReportFilterType reportFilterType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok().body(successResponse( adminService.getFilteredAccounts(startDate, endDate, status, reportFilterType, pageable)));
    }

    @GetMapping("/account/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserInfo(
        @PathVariable Long userId
    ) throws IOException {
        return ResponseEntity.ok().body(successResponse(userService.findById(userId)));
        //return ResponseEntity.ok().body(successResponse(new User()));
    }

    @GetMapping("/account/{itemId}")
    public ResponseEntity<ApiResponse<ItemDto>> getItemInfo(
        @PathVariable Long itemId,
        @RequestParam(required = true) Long userId
    ) {
        return ResponseEntity.ok().body(successResponse(itemService.get(itemId, userId)));
    }

    @PostMapping("/account/add")
    public ResponseEntity<ApiResponse<?>> addReport(
        @ModelAttribute CreateReportRequestDto request
    ) {
        return ResponseEntity.ok().body(successResponse(adminService.addReport(request)));
    }

    @PostMapping("/account/delete")
    public ResponseEntity<ApiResponse<?>> deleteReport(
        @RequestBody(required = true) List<Long> itemIdList,
        @RequestBody(required = true) List<Long> userIdList,
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < itemIdList.size() ; i++) {
            adminService.deleteReport(userIdList.get(i), itemIdList.get(i), reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("삭제 처리"));
    }
    
    @PostMapping("/account/invalid")
    public ResponseEntity<ApiResponse<?>> invalidReport(
        @RequestBody(required = true) List<Long> itemIdList,
        @RequestBody(required = true) List<Long> userIdList,
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < itemIdList.size() ; i++) {
            adminService.invalidReport(userIdList.get(i), itemIdList.get(i), reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("무효 처리"));
    }

    @PostMapping("/account/delete")
    public ResponseEntity<ApiResponse<?>> checkReport(
        @RequestBody(required = true) List<Long> itemIdList,
        @RequestBody(required = true) List<Long> userIdList,
        @RequestBody(required = true) List<Long> reportIdList
    ) {
        for(int i = 0; i < itemIdList.size() ; i++) {
            adminService.checkReport(userIdList.get(i), itemIdList.get(i), reportIdList.get(i));
        }

        return ResponseEntity.ok().body(successResponse("검토"));
    }

    
}
