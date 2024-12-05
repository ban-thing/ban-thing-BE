package com.example.banthing.domain.user.repository;

import com.example.banthing.domain.user.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<ProfileImage, Long> {

    List<ProfileImage> findByType(String type);
}
