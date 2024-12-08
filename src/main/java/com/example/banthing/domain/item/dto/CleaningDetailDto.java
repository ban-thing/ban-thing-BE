package com.example.banthing.domain.item.dto;

import com.example.banthing.domain.item.entity.CleaningDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CleaningDetailDto {
    private String pollution;
    private String timeUsed;
    private String purchasedDate;
    private String cleaned;
    private String expire;

    public static CleaningDetailDto fromEntity(CleaningDetail cleaningDetail) {
        return new CleaningDetailDto(
                cleaningDetail.getPollution(),
                cleaningDetail.getTimeUsed(),
                cleaningDetail.getPurchasedDate(),
                cleaningDetail.getCleaned(),
                cleaningDetail.getExpire()
        );
    }
}
