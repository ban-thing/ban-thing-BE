package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.dto.*;
import com.example.banthing.domain.user.entity.ProfileImage;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.repository.ProfileRepository;
import com.example.banthing.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public ProfileResponseDto findMyProfile(Long userId) {
        return new ProfileResponseDto(findById(userId));
    }

    @Transactional
    public UpdateProfileResponseDto updateMyProfile(Long userId, MultipartFile file, UpdateNicknameRequestDto request) throws IOException {
        User user = findById(userId);

        if (!file.isEmpty()) {
            if (!user.getProfileImg().getType().equals("default"))
                profileRepository.delete(user.getProfileImg());

            ProfileImage image = profileRepository.save(ProfileImage.builder()
                    .data(file.getBytes())
                    .build());
            user.updateProfileImg(image);
        }

        if (request != null) {
            user.updateNickname(request.getNickname());
            System.out.println(user.getNickname());
        }

        return new UpdateProfileResponseDto(user);
    }

    public List<PurchaseResponseDto> findPurchasesById(Long userId) {
        User user = findById(userId);
        return user.getPurchases().stream().map(PurchaseResponseDto::new).toList();
    }

    public List<SalesResponseDto> findSalesById(Long userId) {
        User user = findById(userId);
        return user.getSales().stream().map(SalesResponseDto::new).toList();
    }

    @Transactional
    public UpdateAddressResponseDto updateAddress(Long userId, UpdateAddressRequestDto request) {
        User user = findById(userId);
        UpdateAddressResponseDto response = new UpdateAddressResponseDto(user);

        user.updateAddress(request);
        response.updateAddress(user);
        return response;
    }

    private User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }
}
