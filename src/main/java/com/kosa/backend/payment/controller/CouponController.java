package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.CouponDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 추가
    @PostMapping("/add")
    public ResponseEntity<Coupon> addCoupon(@RequestBody CouponDTO couponDTO) {
        Coupon coupon = couponService.addCoupon(couponDTO);
        return ResponseEntity.ok(coupon);
    }

    // 특정 유저의 쿠폰 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Coupon>> getCouponsByUserId(@PathVariable int userId) {
        List<Coupon> coupons = couponService.getCouponsByUserId(userId);
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 삭제
    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable int couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
