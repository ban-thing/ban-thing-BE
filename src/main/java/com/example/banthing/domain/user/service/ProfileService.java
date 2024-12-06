package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.entity.ProfileImage;
import com.example.banthing.domain.user.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public void uploadImage(MultipartFile file) throws IOException {
        ProfileImage image = ProfileImage.builder()
                .data(file.getBytes())
                .type("default")
                .build();
        profileRepository.save(image);
    }
}
