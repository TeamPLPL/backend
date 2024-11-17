package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingImgListDTO {
    private String thumbnailImgUrl;
    private List<String> detailImgUrlList;
}
