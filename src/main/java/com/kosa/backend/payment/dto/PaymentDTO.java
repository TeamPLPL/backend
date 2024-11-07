// PaymentDTO.java
package com.kosa.backend.payment.dto;

import com.kosa.backend.payment.entity.enums.PaymentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private int amount;
    private LocalDateTime paymentDate;
    private PaymentStatus status;
    private String deliveryAddress;
    private String phoneNum;
    private String receiverName;
    private int userId;
    private Integer couponId;
    private String cardNumber;
    private String methodType;  // 추가: 카드인지 서드파티 페이인지 구분
    private String thirdPartyId; // 서드파티 페이용 ID
    private String thirdPartyPw; // 서드파티 페이용 PW
}
