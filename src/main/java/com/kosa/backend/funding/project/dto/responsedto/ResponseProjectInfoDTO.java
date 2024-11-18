package com.kosa.backend.funding.project.dto.responsedto;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProjectInfoDTO {
    // 메이켜 유형
    String makerType;

    // 대표자 이름, 이메일
    String repName;
    String repEmail;

    // 개인 신분증
    String identityCard;

    // 사업자 신분증
    String businessRegistNum;
    String businessRegistCertif;
    String companyName;

    // 펀딩 설명
    String fundingExplanation;

    // 펀딩 태그
    String fundingTag;
}
