package com.example.banthing.domain.user.repository;

import com.example.banthing.domain.user.entity.UserDeletionReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDeletionReasonRepository extends JpaRepository<UserDeletionReason, Long>, UserDeletionReasonQueryRepository {

}
