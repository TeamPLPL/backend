package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardInfoDTO;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, columnDefinition = "TEXT")
    private String refundsPolicies;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    @Builder
    public RewardInfo(String modelName, String productMaterial, String color, String field, String manufacturer, String manufacturingCountry, String manufactureDate, String refundsPolicies, Funding funding) {
        this.modelName = modelName;
        this.productMaterial = productMaterial;
        this.color = color;
        this.field = field;
        this.manufacturer = manufacturer;
        this.manufacturingCountry = manufacturingCountry;
        this.manufactureDate = manufactureDate;
        this.refundsPolicies = refundsPolicies;
        this.funding = funding;
    }

    public static RewardInfo toSaveEntity(RequestRewardInfoDTO rewardInfoDTO, Funding funding) {
        return RewardInfo.builder()
                .modelName(rewardInfoDTO.getModelName())
                .productMaterial(rewardInfoDTO.getProductMaterial())
                .color(rewardInfoDTO.getColor())
                .field(rewardInfoDTO.getField())
                .manufacturer(rewardInfoDTO.getManufacturer())
                .manufacturingCountry(rewardInfoDTO.getManufacturingCountry())
                .manufactureDate(rewardInfoDTO.getManufactureDate())
                .refundsPolicies(rewardInfoDTO.getRefundsPolicies())
                .funding(funding)
                .build();
    }
}
