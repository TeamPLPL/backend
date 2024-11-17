package com.kosa.backend.funding.project.dto;

import com.kosa.backend.user.dto.FundingMakerDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingDataDTO {
    private int fundingId;
    private String fundingTitle;
    private int currentAmount;
    private int targetAmount;
    private double achievementRate;
    private int supportCnt;
    private String fundingTag;
    private String fundingExplanation;

    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;

   // 펀딩이 속한 카테고리 정보
    private int mainCategoryId;
    private String mainCategoryNm;
    private int subCategoryId;
    private String subCategoryNm;

    private boolean isWishlist; // 로그인한 유저일 경우 내가 찜한 펀딩프로젝트인지

    private FundingMakerDTO makerDTO;
}
