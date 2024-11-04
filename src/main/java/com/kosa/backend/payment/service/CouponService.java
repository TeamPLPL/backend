package com.kosa.backend.payment.service;

import com.kosa.backend.payment.dto.CouponDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.repository.CouponRepository;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    // 쿠폰 추가
    public Coupon addCoupon(CouponDTO couponDTO) {
        User user = userRepository.findById(couponDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + couponDTO.getUserId()));

        Coupon coupon = new Coupon();
        coupon.setCouponName(couponDTO.getCouponName());
        coupon.setDiscountRate(couponDTO.getDiscountRate());
        coupon.setIssueDate(couponDTO.getIssueDate());
        coupon.setUser(user);

        return couponRepository.save(coupon);
    }

    // 특정 유저의 쿠폰 조회
    public List<Coupon> getCouponsByUserId(Long userId) {
        return couponRepository.findByUser_Id(userId);
    }

    // 쿠폰 삭제
    public void deleteCoupon(int couponId) {
        if (couponRepository.existsById(couponId)) {
            couponRepository.deleteById(couponId);
        } else {
            throw new RuntimeException("Coupon not found with ID: " + couponId);
        }
    }
}
