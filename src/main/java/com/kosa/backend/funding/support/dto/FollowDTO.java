package com.kosa.backend.funding.support.dto;

import com.kosa.backend.common.dto.FileDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FollowDTO {
    private int id;
    private String name; // 닉네임
    private FileDTO avatar;   // 프로필 이미지
    private String description; // 설명
    private int makerId; // Maker의 ID
}