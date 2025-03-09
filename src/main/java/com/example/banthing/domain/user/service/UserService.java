package com.example.banthing.domain.user.service;

import com.example.banthing.domain.chat.repository.ChatroomRepository;
import com.example.banthing.domain.chat.service.ChatMessageService;
import com.example.banthing.domain.chat.service.ChatroomService;
import com.example.banthing.domain.item.service.ItemService;
import com.example.banthing.domain.user.dto.*;
import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserDeletionReason;
import com.example.banthing.domain.user.repository.UserRepository;
import com.example.banthing.domain.wishlist.service.WishlistService;
import com.example.banthing.global.common.Timestamped;
import com.example.banthing.global.s3.S3Service;
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
    private final UserDeletionReasonService userDeletionReasonService;
    private final WishlistService wishlistService;
    private final ChatMessageService chatMessageService;
    private final ItemService itemService;
    private final S3Service s3Service;

    public ProfileResponseDto findMyProfile(Long userId) {
        User user = findById(userId);

        // 프로필 이미지 Base64 변환
        String base64ProfileImg = null;
        if (user.getProfileImg() != null && !user.getProfileImg().isEmpty()) {
            try {
                base64ProfileImg = s3Service.encodeImageToBase64(user.getProfileImg(), "profileImage");
            } catch (IOException e) {
                throw new RuntimeException("Failed to encode profile image to Base64", e);
            }
        }
        return new ProfileResponseDto(user, base64ProfileImg);
    }

    @Transactional
    public UpdateProfileResponseDto updateMyProfile(Long userId, MultipartFile file, String nickname) throws IOException {
        User user = findById(userId);

        // 프로필 이미지 변경
        if (file != null && !file.isEmpty()) {
            String folderPath = "profileImage/" + userId;

            // 기존 프로필 이미지가 기본 이미지가 아니면 S3에서 삭제
            if (!user.getProfileImg().startsWith("profileImage/defaultProfileImage")) {
                s3Service.deleteImage(user.getProfileImg());
            }

            String fileName = s3Service.uploadImage(folderPath, file);
            user.updateProfileImg(userId + "/" + fileName);
        }
        if (nickname != null) {
            user.updateNickname(nickname);
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
