// PaymentService.java
package com.kosa.backend.payment.service;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.entity.PaymentHistory;
import com.kosa.backend.payment.entity.PaymentMethod;
import com.kosa.backend.payment.entity.enums.PaymentStatus;
import com.kosa.backend.payment.repository.CouponRepository;
import com.kosa.backend.payment.repository.PaymentHistoryRepository;
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
    private final FundingRepository fundingRepository;
    private final RewardRepository rewardRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
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

        // Funding 조회
        Funding funding = fundingRepository.findById(paymentDTO.getFundingId())
                .orElseThrow(() -> new RuntimeException("Funding not found for ID: " + paymentDTO.getFundingId()));

        // Payment 엔티티 생성 및 저장
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.pending);
        payment.setDeliveryAddress(paymentDTO.getDeliveryAddress());
        payment.setPhoneNum(paymentDTO.getPhoneNum());
        payment.setReceiverName(paymentDTO.getReceiverName());
        payment.setDeliveryRequest(paymentDTO.getDeliveryRequest());
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

        // PaymentHistory 생성 및 저장
        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(savedPayment);
        paymentHistory.setFunding(funding);
        paymentHistoryRepository.save(paymentHistory);

        // 반환할 rewardIds 데이터 가져오기
        List<Integer> rewardIds = paymentDTO.getRewardIds();

        // PaymentDTO 반환
        PaymentDTO resultDTO = new PaymentDTO();
        resultDTO.setUserId(user.getId());
        resultDTO.setAmount(savedPayment.getAmount());
        resultDTO.setDeliveryAddress(savedPayment.getDeliveryAddress());
        resultDTO.setPhoneNum(savedPayment.getPhoneNum());
        resultDTO.setReceiverName(savedPayment.getReceiverName());
        resultDTO.setDeliveryRequest(savedPayment.getDeliveryRequest());  // 매핑 추가
        resultDTO.setCouponId(paymentDTO.getCouponId());
        resultDTO.setMethodType(paymentMethod.getMethodType());
        resultDTO.setCardNumber(paymentMethod.getCardNumber());
        resultDTO.setThirdPartyId(paymentMethod.getThirdPartyId());
        resultDTO.setThirdPartyPw(paymentMethod.getThirdPartyPw());
        resultDTO.setPaymentDate(savedPayment.getPaymentDate());
        resultDTO.setStatus(savedPayment.getStatus().toString());
        resultDTO.setFundingId(funding.getId());
        resultDTO.setRewardIds(rewardIds);  // 매핑 추가

        return resultDTO;
    }

    // 사용자 ID에 따른 결제 이력 조회
    @Transactional
    public List<PaymentDTO> getPaymentsByUserId(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Payment> payments = paymentRepository.findByUser(user);

        return payments.stream().map(payment -> {
            PaymentMethod paymentMethod = paymentMethodRepository.findByPayment(payment)
                    .orElseThrow(() -> new RuntimeException("PaymentMethod not found for Payment ID: " + payment.getId()));

            PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
                    .orElseThrow(() -> new RuntimeException("PaymentHistory not found for Payment ID: " + payment.getId()));

            Funding funding = paymentHistory.getFunding();

            // PaymentDTO 매핑
            PaymentDTO dto = new PaymentDTO();
            dto.setUserId(payment.getUser().getId());
            dto.setAmount(payment.getAmount());
            dto.setDeliveryAddress(payment.getDeliveryAddress());
            dto.setPhoneNum(payment.getPhoneNum());
            dto.setReceiverName(payment.getReceiverName());
            dto.setDeliveryRequest(payment.getDeliveryRequest());
            dto.setPaymentDate(payment.getPaymentDate());
            dto.setStatus(payment.getStatus().toString());
            dto.setMethodType(paymentMethod.getMethodType());
            dto.setCardNumber(paymentMethod.getCardNumber());
            dto.setThirdPartyId(paymentMethod.getThirdPartyId());
            dto.setThirdPartyPw(paymentMethod.getThirdPartyPw());
            dto.setFundingId(funding.getId());

            // Reward ID 리스트 매핑
            List<Integer> rewardIds = funding.getRewards().stream()
                    .map(Reward::getId)  // reward.getId()를 호출할 수 있는지 확인
                    .toList();
            dto.setRewardIds(rewardIds);

            return dto;
        }).toList();
    }



    // 결제 ID에 따른 결제 이력 삭제
    @Transactional
    public void deletePaymentByUser(int paymentId, int userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        if (payment.getUser().getId() != userId) {
            throw new RuntimeException("User does not have permission to delete this payment.");
        }

        // PaymentHistory 삭제
        paymentHistoryRepository.deleteByPayment(payment);

        // PaymentMethod 삭제
        paymentMethodRepository.deleteByPayment(payment);

        // Payment 삭제
        paymentRepository.delete(payment);
    }

}
