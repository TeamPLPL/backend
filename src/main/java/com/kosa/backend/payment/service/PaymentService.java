// PaymentService.java
package com.kosa.backend.payment.service;

import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.entity.PaymentMethod;
import com.kosa.backend.payment.entity.enums.PaymentStatus;
import com.kosa.backend.payment.repository.CouponRepository;
import com.kosa.backend.payment.repository.PaymentMethodRepository;
import com.kosa.backend.payment.repository.PaymentRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    public Payment createPayment(PaymentDTO paymentDTO) {
        // User 조회
        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + paymentDTO.getUserId()));

        // Coupon 조회 (Optional)
        Coupon coupon = null;
        if (paymentDTO.getCouponId() != null) {
            coupon = couponRepository.findById(paymentDTO.getCouponId())
                    .orElseThrow(() -> new RuntimeException("Coupon not found for ID: " + paymentDTO.getCouponId()));
        }

        // Payment 엔티티 생성 및 저장
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.pending);
        payment.setDeliveryAddress(paymentDTO.getDeliveryAddress());
        payment.setPhoneNum(paymentDTO.getPhoneNum());
        payment.setReceiverName(paymentDTO.getReceiverName());
        payment.setUser(user);
        payment.setCoupon(coupon);

        Payment savedPayment = paymentRepository.save(payment);

        // PaymentMethod 생성 및 저장
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodType(paymentDTO.getMethodType());

        if ("CARD".equals(paymentDTO.getMethodType())) {
            paymentMethod.setCardNumber(paymentDTO.getCardNumber());
        } else {
            paymentMethod.setThirdPartyId(paymentDTO.getThirdPartyId());
            paymentMethod.setThirdPartyPw(paymentDTO.getThirdPartyPw());
        }

        paymentMethod.setPayment(savedPayment);
        paymentMethodRepository.save(paymentMethod);

        return savedPayment;
    }
}
