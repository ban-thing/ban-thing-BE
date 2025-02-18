package com.example.banthing.domain.user.service;

import com.example.banthing.domain.chat.entity.Chatroom;
import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.chat.service.ChatMessageService;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.domain.item.entity.Item;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.dto.*;
import com.example.banthing.domain.user.entity.ProfileImage;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserDeletionReason;
import com.example.banthing.domain.user.repository.ProfileRepository;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.domain.wishlist.service.WishlistService;
import com.example.banthing.global.common.Timestamped;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ChatroomRepository chatroomRepository;
    private final ChatroomService chatroomService;
    private final ProfileRepository profileRepository;
    private final UserDeletionReasonService userDeletionReasonService;
    private final WishlistService wishlistService;
    private final ChatMessageService chatMessageService;
    private final ItemService itemService;

    public ProfileResponseDto findMyProfile(Long userId) {
        return new ProfileResponseDto(findById(userId));
    }

    @Transactional
    public UpdateProfileResponseDto updateMyProfile(Long userId, MultipartFile file, String nickname) throws IOException {
        User user = findById(userId);

        if (!file.isEmpty()) {
            if (!user.getProfileImg().getType().equals("default"))
                profileRepository.delete(user.getProfileImg());

            ProfileImage image = profileRepository.save(ProfileImage.builder()
                    .data(file.getBytes())
                    .type("set")
                    .build());
            user.updateProfileImg(image);
        }

        if (nickname != null) {
            user.updateNickname(nickname);
            System.out.println(user.getNickname());
        }

        return new UpdateProfileResponseDto(user);
    }

    public List<PurchaseResponseDto> findPurchasesById(Long userId) {
        User user = findById(userId);
        return chatroomRepository.findAllByBuyerId(user.getId()).stream()
                .sorted(Comparator.comparing(chatroom -> chatroom.getItem().getCreatedAt(), Comparator.reverseOrder()))
                .map(PurchaseResponseDto::new)
                .toList();
    }

    public List<SalesResponseDto> findSalesById(Long userId) {
        User user = findById(userId);
        return user.getSales().stream()
                .sorted(Comparator.comparing(Timestamped::getCreatedAt, Comparator.reverseOrder()))
                .map(SalesResponseDto::new).toList();
    }

    @Transactional
    public UpdateAddressResponseDto updateAddress(Long userId, UpdateAddressRequestDto request) {
        User user = findById(userId);
        UpdateAddressResponseDto response = new UpdateAddressResponseDto(user);

        user.updateAddress(request);
        response.updateAddress(user);
        return response;
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NullPointerException("해당 유저는 존재하지 않습니다."));
    }

    @Transactional
    public void deleteUser(Long userId, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다: " + userId));

        UserDeletionReason deletionReason = new UserDeletionReason(reason);
        userDeletionReasonService.save(deletionReason);

        // 관련된 데이터 삭제
        chatMessageService.deleteBySenderId(userId);
        wishlistService.deleteByUserId(userId);
        chatroomService.deleteByBuyerOrSeller(user);
        itemService.deleteByBuyerOrSeller(user);
        //사용자 삭제
        userRepository.delete(user);
    }

}
