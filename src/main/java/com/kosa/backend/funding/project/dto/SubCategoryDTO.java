package com.kosa.backend.funding.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubCategoryDTO {
    private int subCategoryId;
    private String subCategoryName;
}
