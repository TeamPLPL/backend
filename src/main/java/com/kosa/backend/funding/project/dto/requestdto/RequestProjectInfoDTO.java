package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.enums.MakerType;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RequestProjectInfoDTO {
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
    // 목표 금액
    int targetAmount;
    // 펀딩 설명
    String fundingExplanation;
    // 펀딩 태그
    String fundingTag;

    /* 펀딩 사진 들어가야함 */
}