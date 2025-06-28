package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.entity.RejoinRestriction;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.RejoinRestrictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RejoinRestrictionService {

    private final RejoinRestrictionRepository rejoinRestrictionRepository;
    private final UserService userService;

    public void addRestrictionByUserId(Long userId) {

        User user = userService.findById(userId);
        Long socialId = user.getSocialId();

        if (rejoinRestrictionRepository.existsBySocialId(socialId)) {
            throw new IllegalArgumentException("이미 재가입 제한된 사용자입니다.");
        }

        RejoinRestriction restriction = RejoinRestriction.builder()
                .userId(user.getId())
                .socialId(socialId)
                .build();

        rejoinRestrictionRepository.save(restriction);
    }

    public boolean existsBySocialId(Long kakaoId) {
        return rejoinRestrictionRepository.existsBySocialId(kakaoId);
    }
}

