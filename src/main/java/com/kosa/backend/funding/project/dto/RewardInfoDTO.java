package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardInfoDTO {
    private String modelName;
    private String productMaterial;
    private String color;
    private String field;
    private String manufacturer;
    private String manufacturingCountry;
    private String manufactureDate;
    private String refundsPolicies;
}
