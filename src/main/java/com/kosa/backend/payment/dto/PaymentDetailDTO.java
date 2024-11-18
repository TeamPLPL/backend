package com.kosa.backend.payment.dto;

import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.user.dto.AddressDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentDetailDTO {
    private int paymentId;
    private int amount;
    private Integer couponId;
    private Integer discountRate;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String receiverName;
    private String phoneNum;
    private String deliveryRequest;

    // 결제수단 정보
    private String methodType;
    private String cardNumber;

    // 펀딩 시작 및 종료일
    private LocalDateTime fundingStartDate;
    private LocalDateTime fundingEndDate;

    private AddressDTO address; // 주소 정보
    private FundingDTO funding; // 펀딩 정보
    private List<RewardDTO> rewards; // 리워드 정보

    // 추가된 필드
    private String mainCategory; // 메인 카테고리 이름
    private String subCategory;  // 서브 카테고리 이름
}
