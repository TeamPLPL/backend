package com.kosa.backend.funding.project.dto;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilesDTO {
    private int id;
    private String path;
    private String originalNm;
    private String savedNm;
    private ImgType imgType;
    private int fundingId; // Funding 엔티티의 ID만 포함
    private int userId; // User 엔티티의 ID만 포함

    // 엔티티 -> DTO 변환 메서드
    public static FilesDTO fromEntity(Files files) {
        if (files == null) {
            return null; // 또는 예외를 던지거나 기본 값을 반환하도록 처리
        }
        return FilesDTO.builder()
                .id(files.getId())
                .path(files.getPath())
                .originalNm(files.getOriginalNm())
                .savedNm(files.getSavedNm())
                .imgType(files.getImgType())
                .fundingId(files.getFunding() != null ? files.getFunding().getId() : null)
                .userId(files.getUser() != null ? files.getUser().getId() : null)
                .build();
    }

}
