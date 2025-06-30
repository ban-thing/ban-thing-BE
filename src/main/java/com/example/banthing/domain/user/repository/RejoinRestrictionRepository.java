package com.example.banthing.domain.user.repository;

import com.example.banthing.domain.user.entity.RejoinRestriction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RejoinRestrictionRepository extends JpaRepository<RejoinRestriction, Long> {
    Optional<RejoinRestriction> findBySocialId(Long socialId);
    boolean existsBySocialId(Long socialId);
}
