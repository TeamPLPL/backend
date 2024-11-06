package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REWARD")
public class Reward extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String rewardName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String explanation;

    @Column(nullable = false)
    private int deliveryFee;

    @Column(nullable = false)
    private LocalDateTime deliveryStartDate;

    private Integer quantityLimit;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    @Builder
    public Reward(String rewardName, int price, String explanation, int deliveryFee, LocalDateTime deliveryStartDate, Integer quantityLimit, Funding funding) {
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
