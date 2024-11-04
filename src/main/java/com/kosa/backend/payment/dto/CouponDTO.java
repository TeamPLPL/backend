package com.kosa.backend.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponDTO {
    private String couponName;
    private int discountRate;
    private LocalDateTime issueDate;
    private int userId;  // 유저 ID
}
