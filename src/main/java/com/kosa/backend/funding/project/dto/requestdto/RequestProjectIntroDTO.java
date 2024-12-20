package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RequestProjectIntroDTO {
    private String fundingTitle;
    private int subCategory;
    private int targetAmount;
}
