package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.Funding;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FundingWithSupporterCntDTO {
    private int id;
    private String fundingTitle;
    private String makerNick; // USER_userNick
    private int supportCnt;
    private double achievementRate;
    private int wishlistCnt;
    private String thumbnailImgUrl;

}
