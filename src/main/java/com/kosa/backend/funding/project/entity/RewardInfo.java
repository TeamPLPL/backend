package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REWARD_INFO")
public class RewardInfo extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String modelName;

    @Column(nullable = false)
    private String productMaterial;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String field;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String manufacturingCountry;

    @Column(nullable = false)
    private String manufactureDate;

    @Column(nullable = false)
    private String refundsPolicies;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;
}
