package com.kosa.backend.funding.support.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FollowDTO {
    private int id;
    private String name; // 닉네임
    private String avatar;   // 프로필 이미지
    private String description; // 설명
}
