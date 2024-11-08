package com.kosa.backend.funding.project.dto.requestdto;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestRewardDTO {
    private int id;
    private String rewardName;
    private int price;
    private String explanation;
    private int deliveryFee;
    private LocalDateTime deliveryStartDate;
    private int quantityLimit;
}
