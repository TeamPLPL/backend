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
    private String explanation;
    private int deliveryFee;
    private LocalDateTime deliveryStartDate;
    private int quantityLimit;
    private int supportedCnt;

    private int count;

    @Builder
    public RewardDTO(int rewardId, String rewardName, int price, LocalDateTime deliveryStartDate, int deliveryFee, int count) {
        this.rewardId = rewardId;
        this.rewardName = rewardName;
        this.price = price;
        this.deliveryStartDate = deliveryStartDate;
        this.deliveryFee = deliveryFee;
        this.count = count;
    }
}
