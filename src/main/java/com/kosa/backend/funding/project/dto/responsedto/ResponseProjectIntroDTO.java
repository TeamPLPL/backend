package com.kosa.backend.funding.project.dto.responsedto;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ResponseProjectIntroDTO {
    private String fundingTitle;
    private int subCategory;
    private int targetAmount;
}
