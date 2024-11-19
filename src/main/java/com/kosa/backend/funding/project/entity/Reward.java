package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REWARD")
public class Reward extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String rewardName;

    private int price;

    private String explanation;

    private int deliveryFee;

    private LocalDateTime deliveryStartDate;

    private Integer quantityLimit;

    @ManyToOne
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @Builder
    public Reward(int id, String rewardName, int price, String explanation, int deliveryFee, LocalDateTime deliveryStartDate, Integer quantityLimit, Funding funding) {
        this.id = id;
        this.rewardName = rewardName;
        this.price = price;
        this.explanation = explanation;
        this.deliveryFee = deliveryFee;
        this.deliveryStartDate = deliveryStartDate;
        this.quantityLimit = quantityLimit;
        this.funding = funding;
    }

    public static Reward toSaveEntity(RequestRewardDTO rewardDTO, Funding funding) {
        return Reward.builder()
                .rewardName(rewardDTO.getRewardName())
                .price(rewardDTO.getPrice())
                .explanation(rewardDTO.getExplanation())
                .deliveryFee(rewardDTO.getDeliveryFee())
                .deliveryStartDate(rewardDTO.getDeliveryStartDate())
                .quantityLimit(rewardDTO.getQuantityLimit())
                .funding(funding)
                .build();
    }
}
