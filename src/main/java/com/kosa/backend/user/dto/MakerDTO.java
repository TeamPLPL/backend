package com.kosa.backend.user.dto;

import com.kosa.backend.user.entity.Maker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MakerDTO {
    private int id;
    private int userId; // User의 ID만 저장
    private String userContent;

    // 엔티티 -> DTO 변환 메서드
    public static MakerDTO fromEntity(Maker maker) {
        return MakerDTO.builder()
                .id(maker.getId())
                .userId(maker.getUser().getId()) // User 엔티티의 ID만 사용
                .userContent(maker.getUserContent())
                .build();
    }
}
