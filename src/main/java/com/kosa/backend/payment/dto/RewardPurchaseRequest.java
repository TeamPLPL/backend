package com.kosa.backend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardPurchaseRequest {
    private int rewardId;
    private int fundingId;
    private int purchaseQuantity;

    // String 타입도 처리 가능하도록 추가
    public void setFundingId(String fundingId) {
        this.fundingId = Integer.parseInt(fundingId);
    }
}
