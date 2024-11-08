package com.kosa.backend.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PaymentDTO {
    private int userId;
    private int amount;
    private String deliveryAddress;
    private String phoneNum;
    private String receiverName;
    private String deliveryRequest;
    private Integer couponId; // Optional coupon ID

    // PaymentMethod 관련 필드
    private String methodType; // 결제 방식 (e.g., "CARD" or "THIRD_PARTY")
    private String cardNumber; // 카드 번호 (methodType이 "CARD"일 때만 사용)
    private String thirdPartyId; // 타사 결제 ID (methodType이 "THIRD_PARTY"일 때 사용)
    private String thirdPartyPw; // 타사 결제 비밀번호 (methodType이 "THIRD_PARTY"일 때 사용)

    private LocalDateTime paymentDate;
    private String status;

    private int fundingId; // 추가: 관련된 Funding ID
    private List<Integer> rewardIds; // 추가: 관련된 Reward IDs
}
