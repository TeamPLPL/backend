package com.kosa.backend.payment.service;

import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import com.kosa.backend.payment.dto.PaymentDTO;
import com.kosa.backend.payment.dto.PaymentDetailDTO;
import com.kosa.backend.payment.entity.*;
import com.kosa.backend.payment.entity.enums.PaymentStatus;
import com.kosa.backend.payment.repository.*;
import com.kosa.backend.user.dto.AddressDTO;
import com.kosa.backend.user.entity.Address;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.AddressRepository;
import com.kosa.backend.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
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
    private final FundingSupportRepository fundingSupportRepository;

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO, User user) {

        // 사용자 조회
//        User user = userRepository.findById(paymentDTO.getUserId())
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + paymentDTO.getUserId()));
        if (user == null) {
            throw new RuntimeException("인증된 사용자가 없습니다.");
        }

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

        if ("card".equals(paymentDTO.getMethodType())) {
            if (paymentDTO.getCardNumber() == null || paymentDTO.getCardNumber().isEmpty()) {
                throw new IllegalArgumentException("카드 결제 시 카드 번호가 필요합니다.");
            }
            paymentMethod.setCardNumber(paymentDTO.getCardNumber());
        } else {
            // Card number should remain null for non-CARD payments
            paymentMethod.setCardNumber(null);
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

        // 새로운 FundingSupport 저장
        paymentDTO.getRewards().forEach((rewardId, rewardInfo) -> {
            Reward reward = rewardRepository.findById(rewardId)
                    .orElseThrow(() -> new RuntimeException("리워드를 찾을 수 없습니다: " + rewardId));

            FundingSupport fundingSupport = FundingSupport.builder()
                    .reward(reward)
                    .funding(funding)
                    .user(user)
                    .payment(savedPayment)  // Payment 설정
                    .rewardCount(rewardInfo.getQuantity())
                    .supportDate(LocalDateTime.now())
                    .build();

            fundingSupportRepository.save(fundingSupport);
        });

        return mapToDTO(savedPayment, funding, paymentDTO.getRewards());
    }

    // 사용자별 거래 내역 조회
    @Transactional
    public List<PaymentDTO> getPaymentsByUserId(int userId) {
        List<Payment> payments = paymentRepository.findByUser_Id(userId);

        return payments.stream().map(payment -> {
            PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
                    .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다: " + payment.getId()));

            Funding funding = paymentHistory.getFunding();

            List<FundingSupport> fundingSupports = fundingSupportRepository.findByFundingIdAndUserIdAndPaymentId(
                    funding.getId(), payment.getUser().getId(), payment.getId()
            );

            Map<Integer, PaymentDTO.RewardInfo> rewardsMap = fundingSupports.stream()
                    .collect(Collectors.toMap(
                            fs -> fs.getReward().getId(),
                            fs -> {
                                PaymentDTO.RewardInfo rewardInfo = new PaymentDTO.RewardInfo();
                                rewardInfo.setRewardId(fs.getReward().getId());
                                rewardInfo.setRewardName(fs.getReward().getRewardName());
                                rewardInfo.setQuantity(fs.getRewardCount());
                                return rewardInfo;
                            },
                            (existing, replacement) -> existing // 중복 시 기존 값 유지
                    ));

            return mapToDTO(payment, funding, rewardsMap);
        }).collect(Collectors.toList());
    }

    // 결제 id 별로 세부 내역 확인
    @Transactional(readOnly = true)
    public PaymentDetailDTO getPaymentDetailsByPaymentId(int paymentId) {
        // Payment 가져오기
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다: " + paymentId));

        // PaymentHistory 가져오기
        PaymentHistory paymentHistory = paymentHistoryRepository.findByPayment(payment)
                .orElseThrow(() -> new RuntimeException("결제 내역을 찾을 수 없습니다: " + paymentId));

        // Funding 초기화
        Funding funding = paymentHistory.getFunding();
        Hibernate.initialize(funding.getMaker()); // Lazy 로딩 방지
        Hibernate.initialize(funding.getSubCategory()); // Lazy 로딩 방지

        // FundingDTO 생성
        FundingDTO fundingDTO = FundingDTO.builder()
                .id(funding.getId())
                .fundingTitle(funding.getFundingTitle())
                .makerNick(funding.getMaker().getUser().getUserNick())
                .build();

        // FundingSupport에서 Reward 정보 가져오기
        List<FundingSupport> fundingSupports = fundingSupportRepository.findByFundingIdAndUserIdAndPaymentId(
                funding.getId(), payment.getUser().getId(), payment.getId()
        );

        List<RewardDTO> rewardDTOs = fundingSupports.stream()
                .map(fs -> RewardDTO.builder()
                        .rewardId(fs.getReward().getId())
                        .rewardName(fs.getReward().getRewardName())
                        .price(fs.getReward().getPrice())
                        .deliveryStartDate(fs.getReward().getDeliveryStartDate())
                        .deliveryFee(fs.getReward().getDeliveryFee())
                        .count(fs.getRewardCount())
                        .build()
                ).collect(Collectors.toList());

        // AddressDTO 생성
        AddressDTO addressDTO = AddressDTO.builder()
                .id(payment.getAddress().getId())
                .zonecode(payment.getAddress().getZonecode())
                .addr(payment.getAddress().getAddr())
                .addrEng(payment.getAddress().getAddrEng())
                .detailAddr(payment.getAddress().getDetailAddr())
                .extraAddr(payment.getAddress().getExtraAddr())
                .isDefault(payment.getAddress().isDefault())
                .userId(payment.getUser().getId())
                .build();

        // PaymentDetailDTO 생성
        PaymentDetailDTO paymentDetailDTO = new PaymentDetailDTO();
        paymentDetailDTO.setPaymentId(payment.getId());
        paymentDetailDTO.setPaymentDate(payment.getPaymentDate());
        paymentDetailDTO.setPaymentStatus(payment.getStatus().name());
        paymentDetailDTO.setReceiverName(payment.getReceiverName());
        paymentDetailDTO.setPhoneNum(payment.getPhoneNum());
        paymentDetailDTO.setDeliveryRequest(payment.getDeliveryRequest());
        paymentDetailDTO.setFundingStartDate(funding.getFundingStartDate());
        paymentDetailDTO.setFundingEndDate(funding.getFundingEndDate());

        paymentDetailDTO.setAddress(addressDTO);
        paymentDetailDTO.setFunding(fundingDTO);
        paymentDetailDTO.setRewards(rewardDTOs);

        return paymentDetailDTO;
    }


    // 결제 상태 수정(거래 성공이나 취소 등의 여부에 따라 변경됨)
    @Transactional
    public void updatePaymentStatus(int paymentId, String status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("결제를 찾을 수 없습니다: " + paymentId));

        payment.setStatus(PaymentStatus.valueOf(status)); // Enum으로 상태 설정
        paymentRepository.save(payment);
    }

    // 거래 내역 삭제(일정 기간이 지난 후 폐기하는 것을 고려할 것)
    @Transactional
    public void deletePaymentByUser(int paymentId, int userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("해당 결제를 찾을 수 없습니다: " + paymentId));

        if (payment.getUser().getId() != userId) {
            throw new RuntimeException("결제를 삭제할 권한이 없습니다.");
        }

        // funding_support에서 참조하는 레코드 삭제
        fundingSupportRepository.deleteByPaymentId(paymentId);

        // PaymentHistory 삭제
        paymentHistoryRepository.deleteByPayment(payment);

        // PaymentMethod 삭제
        paymentMethodRepository.deleteByPayment(payment);

        // Payment 삭제
        paymentRepository.delete(payment);
    }

    private PaymentDTO mapToDTO(Payment payment, Funding funding, Map<Integer, PaymentDTO.RewardInfo> rewardsMap) {
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

        // Reward 정보 추가 (Map 사용)
        dto.setRewards(rewardsMap);

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
