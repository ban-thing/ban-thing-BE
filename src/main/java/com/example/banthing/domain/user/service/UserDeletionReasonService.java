package com.example.banthing.domain.user.service;

import com.example.banthing.domain.user.entity.User;
import com.example.banthing.domain.user.entity.UserDeletionReason;
import com.example.banthing.domain.user.repository.UserDeletionReasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeletionReasonService {

    private final UserDeletionReasonRepository userDeletionReasonRepository;

    public void save(UserDeletionReason deletionReason) {
        userDeletionReasonRepository.save(deletionReason);
    }
}