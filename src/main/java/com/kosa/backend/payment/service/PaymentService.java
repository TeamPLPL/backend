// PaymentService.java
package com.kosa.backend.payment.service;

import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.entity.PaymentMethod;
import com.kosa.backend.payment.entity.eunms.PaymentStatus;
import com.kosa.backend.payment.repository.CouponRepository;
import com.kosa.backend.payment.repository.PaymentMethodRepository;
import com.kosa.backend.payment.repository.PaymentRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
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

        // PaymentDTO 반환을 위한 매핑
        PaymentDTO resultDTO = new PaymentDTO();
        resultDTO.setUserId(user.getId());
        resultDTO.setAmount(savedPayment.getAmount());
        resultDTO.setDeliveryAddress(savedPayment.getDeliveryAddress());
        resultDTO.setPhoneNum(savedPayment.getPhoneNum());
        resultDTO.setReceiverName(savedPayment.getReceiverName());
        resultDTO.setCouponId(paymentDTO.getCouponId());
        resultDTO.setMethodType(paymentMethod.getMethodType());
        resultDTO.setCardNumber(paymentMethod.getCardNumber()); // 카드 번호 포함
        resultDTO.setThirdPartyId(paymentMethod.getThirdPartyId());
        resultDTO.setThirdPartyPw(paymentMethod.getThirdPartyPw());
        resultDTO.setPaymentDate(savedPayment.getPaymentDate());
        resultDTO.setStatus(savedPayment.getStatus().toString());

        return resultDTO;
    }

    // 사용자 ID에 따른 결제 이력 조회
    public List<Payment> getPaymentsByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        return paymentRepository.findByUser(user);
    }

    // 결제 ID에 따른 결제 이력 삭제
    @Transactional
    public void deletePaymentByUser(int paymentId, int userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getUser().getId() == userId) {
            paymentMethodRepository.deleteByPaymentAndPaymentUserId(payment, userId);
            paymentRepository.delete(payment);
        } else {
            throw new RuntimeException("User does not have permission to delete this payment.");
        }

        // PaymentMethod 삭제
        paymentMethodRepository.deleteByPaymentAndPaymentUserId(payment, userId);

        // Payment 삭제
        paymentRepository.delete(payment);
    }
}
