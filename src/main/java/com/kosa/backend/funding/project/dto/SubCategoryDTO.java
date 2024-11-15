package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubCategoryDTO {
    private int subCategoryId;
    private String subCategoryName;

    public static SubCategoryDTO fromSubCategory(SubCategory subCategory) {
        SubCategoryDTO subCategoryDTO = new SubCategoryDTO();
        subCategoryDTO.setSubCategoryId(subCategory.getId());
        subCategoryDTO.setSubCategoryName(subCategoryDTO.getSubCategoryName());

        return subCategoryDTO;
    }

    // 작성자 : 신은호, 작성 내용 : subcategory entity 변환
    private int mainCategory;

    public static SubCategoryDTO fromSubCategories(SubCategory subCategory) {
        return SubCategoryDTO.builder()
                .subCategoryId(subCategory.getId())
                .subCategoryName(subCategory.getSubCategoryName())
                .mainCategory(subCategory.getMainCategory().getId())
                .build();
    }

}
