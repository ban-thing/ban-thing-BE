package com.example.banthing.domain.item.service;

import com.example.banthing.domain.item.entity.CleaningDetail;
import com.example.banthing.domain.item.repository.CleaningDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CleaningDetailService {

    private final CleaningDetailRepository cleaningDetailRepository;

    public void update(Long id, CleaningDetail updatedCleaningDetail) {
        CleaningDetail existingCleaningDetail = cleaningDetailRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("CleaningDetail을 찾을 수 없습니다."));

        existingCleaningDetail = existingCleaningDetail.toBuilder()
                .pollution(updatedCleaningDetail.getPollution())
                .timeUsed(updatedCleaningDetail.getTimeUsed())
                .purchasedDate(updatedCleaningDetail.getPurchasedDate())
                .cleaned(updatedCleaningDetail.getCleaned())
                .expire(updatedCleaningDetail.getExpire())
                .build();

        cleaningDetailRepository.save(existingCleaningDetail);
    }
}
