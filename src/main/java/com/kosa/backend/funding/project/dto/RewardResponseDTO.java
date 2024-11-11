package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class RewardResponseDTO {
    private List<RewardDTO> rewardDTOList;
    private int deliveryFee;
}
