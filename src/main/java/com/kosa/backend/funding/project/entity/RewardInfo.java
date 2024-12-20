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

    private String modelName;

    private String productMaterial;

    private String color;

    private String field;

    private String manufacturer;

    private String manufacturingCountry;

    private String manufactureDate;

    @Column(columnDefinition = "TEXT")
    private String refundsPolicies;

    @OneToOne
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @Builder
    public RewardInfo(String modelName, String productMaterial, String color, String field, String manufacturer, String manufacturingCountry, String manufactureDate, Funding funding) {
        this.modelName = modelName;
        this.productMaterial = productMaterial;
        this.color = color;
        this.field = field;
        this.manufacturer = manufacturer;
        this.manufacturingCountry = manufacturingCountry;
        this.manufactureDate = manufactureDate;
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
                .funding(funding)
                .build();
    }

    @PrePersist
    public void prePersist() {
        if (this.refundsPolicies == null) {
            this.refundsPolicies = "환불·정책\n" +
                    "1. 결제 취소 및 환불 안내\n" +
                    "2. 프로젝트 종료 전 까지 언제든 결제 취소 가능해요.\n" +
                    "3. 환불은 참여 내역에서 신청할 수 있어요.\n" +
                    "4. 환불 신청은 리워드 수령(배송 완료) 후 7일 이내 가능해요.\n" +
                    "5. 환불 신청 후 메이커와 소통하여 리워드를 발송해주세요.\n" +
                    "6. 단순변심: 반품비 서포터 부담\n" +
                    "7. 리워드 품질 하자: 반품비 메이커 부담\n";
        }
    }

    // 각 필드에 대한 update 메서드
    public void updateModelName(String modelName) {
        this.modelName = modelName;
    }

    public void updateProductMaterial(String productMaterial) {
        this.productMaterial = productMaterial;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void updateField(String field) {
        this.field = field;
    }

    public void updateManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void updateManufacturingCountry(String manufacturingCountry) {
        this.manufacturingCountry = manufacturingCountry;
    }

    public void updateManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }
}
