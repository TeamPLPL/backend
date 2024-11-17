package com.kosa.backend.payment.service;

import com.kosa.backend.payment.dto.CouponDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.repository.CouponRepository;
import com.kosa.backend.payment.repository.PaymentRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    // 쿠폰 추가
    public CouponDTO addCoupon(CouponDTO couponDTO) {
        User user = userRepository.findById(couponDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + couponDTO.getUserId()));

        Coupon coupon = new Coupon();
        coupon.setCouponName(couponDTO.getCouponName());
        coupon.setDiscountRate(couponDTO.getDiscountRate());
        coupon.setIssueDate(couponDTO.getIssueDate());
        coupon.setUser(user);

        Coupon savedCoupon = couponRepository.save(coupon);

        // 저장된 Coupon을 DTO로 변환
        return mapToDTO(savedCoupon);
    }

    // 특정 유저의 쿠폰 조회
    public List<Coupon> getCouponsByUserId(int userId) {
        // 모든 쿠폰 조회
        List<Coupon> allCoupons = couponRepository.findByUser_Id(userId);

        // 사용된 쿠폰 ID 조회
        List<Integer> usedCouponIds = paymentRepository.findByUser_Id(userId).stream()
                .map(Payment::getCoupon)
                .filter(Objects::nonNull)
                .map(Coupon::getId)
                .toList();

        // 사용되지 않은 쿠폰 필터링
        return allCoupons.stream()
                .filter(coupon -> !usedCouponIds.contains(coupon.getId()))
                .collect(Collectors.toList()); // 엔터티 반환
    }

    // 쿠폰 삭제
    public void deleteCoupon(int couponId) {
        if (couponRepository.existsById(couponId)) {
            couponRepository.deleteById(couponId);
        } else {
            throw new RuntimeException("Coupon not found with ID: " + couponId);
        }
    }

    private CouponDTO mapToDTO(Coupon coupon) {
        CouponDTO dto = new CouponDTO();
        dto.setId(coupon.getId());
        dto.setCouponName(coupon.getCouponName());
        dto.setDiscountRate(coupon.getDiscountRate());
        dto.setIssueDate(coupon.getIssueDate());
        dto.setUserId(coupon.getUser().getId());

        return dto;
    }
}
