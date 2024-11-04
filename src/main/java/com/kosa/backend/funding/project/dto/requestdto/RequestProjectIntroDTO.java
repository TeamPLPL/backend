package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.SubCategory;
import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RequestProjectIntroDTO {
    private int id;
    private String fundingTitle;
    private SubCategory subCategory;

}
