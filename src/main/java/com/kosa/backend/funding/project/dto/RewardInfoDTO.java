package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.RewardInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardInfoDTO {
    private int id;
    private String modelName;
    private String productMaterial;
    private String color;
    private String field;
    private String manufacturer;
    private String manufacturingCountry;
    private String manufactureDate;
    private String refundsPolicies;
    private int fundingId; // Funding 엔터티와의 관계를 ID로 표현

    public static RewardInfoDTO fromEntity(RewardInfo rewardInfo) {
        return RewardInfoDTO.builder()
                .id(rewardInfo.getId())
                .modelName(rewardInfo.getModelName())
                .productMaterial(rewardInfo.getProductMaterial())
                .color(rewardInfo.getColor())
                .field(rewardInfo.getField())
                .manufacturer(rewardInfo.getManufacturer())
                .manufacturingCountry(rewardInfo.getManufacturingCountry())
                .manufactureDate(rewardInfo.getManufactureDate())
                .refundsPolicies(rewardInfo.getRefundsPolicies())
                .fundingId(rewardInfo.getFunding().getId()) // Funding 엔터티에서 ID 추출
                .build();
    }
}
