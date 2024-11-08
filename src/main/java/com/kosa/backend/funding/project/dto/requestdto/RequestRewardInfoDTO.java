package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.RewardInfo;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestRewardInfoDTO {
    private String modelName;
    private String productMaterial;
    private String color;
    private String field;
    private String manufacturer;
    private String manufacturingCountry;
    private String manufactureDate;
}
