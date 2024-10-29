package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
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
}
