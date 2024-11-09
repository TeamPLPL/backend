package com.kosa.backend.funding.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class RewardDTO {
    private int rewardId;
    private String rewardName;
    private int price;
    private LocalDateTime deliveryStartDate;

    private int count;
}
