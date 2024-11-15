package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.BusinessMaker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessMakerDTO {
    private int id;
    private String businessRegistNum;
    private String businessRegistCertif;
    private String companyName;

    // 엔티티 -> DTO 변환 메서드
    public static BusinessMakerDTO fromEntity(BusinessMaker businessMaker) {
        return BusinessMakerDTO.builder()
                .id(businessMaker.getId())
                .businessRegistNum(businessMaker.getBusinessRegistNum())
                .businessRegistCertif(businessMaker.getBusinessRegistCertif())
                .companyName(businessMaker.getCompanyName())
                .build();
    }
}
