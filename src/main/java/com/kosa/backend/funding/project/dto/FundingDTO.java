package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingDTO {
    private int id;
    private String fundingTitle;
    private String makerNick; // USER_userNick
    private int supportCnt;
    private double achievementRate;
    private int wishlistCnt;
    private InputStreamResource thumbnailImg;
}
