package com.kosa.backend.payment.service;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.entity.*;
import com.kosa.backend.payment.entity.enums.PaymentStatus;
import com.kosa.backend.payment.repository.*;
import com.kosa.backend.user.entity.Address;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.AddressRepository;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final FundingRepository fundingRepository;
    private final AddressRepository addressRepository;
    private final RewardRepository rewardRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        // 사용자 조회
        User user = userRepository.findById(paymentDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + paymentDTO.getUserId()));

        // 주소 조회
        Address address = addressRepository.findById(paymentDTO.getAddressId())
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다: " + paymentDTO.getAddressId()));

        // 쿠폰 조회 (Optional)
        Coupon coupon = paymentDTO.getCouponId() != null
                ? couponRepository.findById(paymentDTO.getCouponId()).orElse(null)
                : null;

        // Payment 생성
        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.pending);
        payment.setPhoneNum(paymentDTO.getPhoneNum());
        payment.setReceiverName(paymentDTO.getReceiverName());
        payment.setDeliveryRequest(paymentDTO.getDeliveryRequest());
        payment.setUser(user);
        payment.setAddress(address);
        payment.setCoupon(coupon);

        Payment savedPayment = paymentRepository.save(payment);

        // 결제 방식 저장
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setMethodType(paymentDTO.getMethodType());
        paymentMethod.setPayment(savedPayment);

        if ("CARD".equals(paymentDTO.getMethodType())) {
            paymentMethod.setCardNumber(paymentDTO.getCardNumber());
        } else {
            paymentMethod.setThirdPartyId(paymentDTO.getThirdPartyId());
            paymentMethod.setThirdPartyPw(paymentDTO.getThirdPartyPw());
        }

        paymentMethodRepository.save(paymentMethod);

        // PaymentHistory 저장
        Funding funding = fundingRepository.findById(paymentDTO.getFundingId())
                .orElseThrow(() -> new RuntimeException("펀딩을 찾을 수 없습니다: " + paymentDTO.getFundingId()));

        PaymentHistory paymentHistory = new PaymentHistory();
        paymentHistory.setPayment(savedPayment);
        paymentHistory.setFunding(funding);

        paymentHistoryRepository.save(paymentHistory);

        // 선택된 리워드 필터링
        List<Reward> selectedRewards = paymentDTO.getRewards().values().stream()
                .map(rewardInfo -> rewardRepository.findById(rewardInfo.getRewardId())
                        .orElseThrow(() -> new RuntimeException("리워드를 찾을 수 없습니다: " + rewardInfo.getRewardId())))
                .collect(Collectors.toList());

        // 반환 DTO 작성
        return mapToDTO(savedPayment, funding, selectedRewards);
    }

    @Transactional
    public List<PaymentDTO> getPaymentsByUserId(int userId) {
        // 특정 사용자의 모든 Payment 기록 조회
        List<Payment> payments = paymentRepository.findByUser_Id(userId);

        return payments.stream().map(payment -> {
            // PaymentHistory 조회
            PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
                    .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다: " + payment.getId()));

            Funding funding = paymentHistory.getFunding();

            // 선택된 리워드 필터링 (PaymentDTO를 통해 선택된 리워드 정보 가져오기)
            Map<Integer, PaymentDTO.RewardInfo> selectedRewardMap = mapToDTO(payment, funding, Collections.emptyList()).getRewards();

            List<Reward> selectedRewards = funding.getRewards().stream()
                    .filter(reward -> selectedRewardMap.containsKey(reward.getId())) // 선택된 리워드만 필터링
                    .collect(Collectors.toList());

            return mapToDTO(payment, funding, selectedRewards);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deletePaymentByUser(int paymentId, int userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("해당 결제를 찾을 수 없습니다: " + paymentId));

        if (payment.getUser().getId() != userId) {
            throw new RuntimeException("결제를 삭제할 권한이 없습니다.");
        }

        // PaymentHistory 삭제
        paymentHistoryRepository.deleteByPayment(payment);

        // PaymentMethod 삭제
        paymentMethodRepository.deleteByPayment(payment);

        // Payment 삭제
        paymentRepository.delete(payment);
    }

    private PaymentDTO mapToDTO(Payment payment, Funding funding, List<Reward> rewards) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUser().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setStatus(payment.getStatus().toString());
        dto.setPhoneNum(payment.getPhoneNum());
        dto.setReceiverName(payment.getReceiverName());
        dto.setDeliveryRequest(payment.getDeliveryRequest());
        dto.setCouponId(payment.getCoupon() != null ? payment.getCoupon().getId() : null);
        dto.setAddressId(payment.getAddress().getId());
        dto.setFundingId(funding.getId());

        // 선택된 리워드를 Map 형식으로 변환
        dto.setRewards(
                rewards.stream()
                        .collect(Collectors.toMap(
                                Reward::getId,
                                reward -> {
                                    PaymentDTO.RewardInfo rewardInfo = new PaymentDTO.RewardInfo();
                                    rewardInfo.setRewardId(reward.getId());
                                    rewardInfo.setRewardName(reward.getRewardName());
                                    return rewardInfo;
                                }
                        ))
        );

        // PaymentMethod 정보 추가
        PaymentMethod paymentMethod = paymentMethodRepository.findByPayment(payment)
                .orElseThrow(() -> new RuntimeException("결제 수단을 찾을 수 없습니다: " + payment.getId()));

        dto.setMethodType(paymentMethod.getMethodType());
        if ("CARD".equals(paymentMethod.getMethodType())) {
            dto.setCardNumber(paymentMethod.getCardNumber());
        } else {
            dto.setThirdPartyId(paymentMethod.getThirdPartyId());
            dto.setThirdPartyPw(paymentMethod.getThirdPartyPw());
        }

        return dto;
    }


}
