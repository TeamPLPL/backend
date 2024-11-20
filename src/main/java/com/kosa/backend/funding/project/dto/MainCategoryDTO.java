package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.MainCategory;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MainCategoryDTO {
    private int mainCategoryId;
    private String mainCategoryName;

    public static MainCategoryDTO fromMainCategory(MainCategory mainCategory) {
        return MainCategoryDTO.builder()
                .mainCategoryId(mainCategory.getId())
                .mainCategoryName(mainCategory.getMainCategoryName())
                .build();
    }
}
