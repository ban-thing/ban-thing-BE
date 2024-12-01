package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.dto.ProfileResponseDto;
import com.example.banthing.domain.user.dto.PurchaseResponseDto;
import com.example.banthing.domain.user.dto.SalesResponseDto;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileResponseDto findMyProfile (Long userId) {
        return new ProfileResponseDto(findById(userId));
    }

    public List<PurchaseResponseDto> findPurchasesById(Long userId) {
        User user = findById(userId);
        return user.getPurchases().stream().map(PurchaseResponseDto::new).toList();
    }

    public List<SalesResponseDto> findSalesById(Long userId) {
        User user = findById(userId);
        return user.getSales().stream().map(SalesResponseDto::new).toList();
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }
}
