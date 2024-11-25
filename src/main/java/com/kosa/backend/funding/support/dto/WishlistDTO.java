package com.kosa.backend.funding.support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistDTO {
    private int wishlistId; // 위시리스트 ID
    private String thumbnailImgUrl;
    private String fundingTitle; // 펀딩 제목
    private String mainCategory; // 메인 카테고리 이름
    private String subCategory; // 서브 카테고리 이름
    private int fundingId; // 펀딩 ID 추가
}
