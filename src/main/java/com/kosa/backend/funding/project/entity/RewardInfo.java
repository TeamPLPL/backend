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
                    "7. 리워드 품질 하자: 반품비 메이커 부담 환불정책보기\n" +
                    "8. 환불 정책에 따라 꼼꼼한 확인 절차를 통해 진행돼요.\n" +
                    "9. 메이커가 리워드 발송 시작 예정일까지 리워드를 발송하지 않을 경우 환불 신청 이후 즉시 결제 취소돼요.(2~5영업일 소요)\n" +
                    "10. 2023년 11월 8일 이전에 종료된 펀딩 프로젝트는 서포터 단순변심에 의한 환불이 불가해요.";
        }
    }
}
