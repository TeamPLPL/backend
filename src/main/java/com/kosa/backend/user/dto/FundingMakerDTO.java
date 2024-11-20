package com.kosa.backend.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FundingMakerDTO {
    // 메이커id, Userid, 프사url, 닉네임, 소개, 팔로우 여부
    private int makerId;
    private int userId;
    private String userContent;
    private String userNick;
    private String profileImgUrl;
    private String makerEmail;

    private boolean isFollowing;
}
