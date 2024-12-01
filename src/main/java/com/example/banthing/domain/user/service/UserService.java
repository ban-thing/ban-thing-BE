package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.dto.ProfileResponseDto;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileResponseDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));

        return new ProfileResponseDto(user);
    }
}
