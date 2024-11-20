package com.kosa.backend.funding.project.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestProjectInfoDTO {
    // 메이커 유형
    String makerType;

    // 대표자 이름, 이메일
    String repName;
    String repEmail;

    // 개인 신분증
    String identityCard;

    // 사업자 신분증
    String businessRegistNum;

    // 사업장 이름
    String companyName;

    // 펀딩 설명
    String fundingExplanation;
    // 펀딩 태그
    String fundingTag;
}
