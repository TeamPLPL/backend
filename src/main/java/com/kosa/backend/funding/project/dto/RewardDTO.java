package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.Reward;
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

    public static RewardDTO fromEntity(Reward reward) {
        return RewardDTO.builder()
                .rewardId(reward.getId())
                .rewardName(reward.getRewardName())
                .price(reward.getPrice())
                .explanation(reward.getExplanation())
                .deliveryFee(reward.getDeliveryFee())
                .quantityLimit(reward.getQuantityLimit())
                .build();
    }
}
