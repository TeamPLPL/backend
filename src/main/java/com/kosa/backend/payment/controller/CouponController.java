package com.kosa.backend.payment.controller;

import com.kosa.backend.payment.dto.CouponDTO;
import com.kosa.backend.payment.entity.Coupon;
import com.kosa.backend.payment.service.CouponService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;
    private final UserService userService;

    // 쿠폰 추가
    @PostMapping("/add")
    public ResponseEntity<CouponDTO> addCoupon(@RequestBody CouponDTO couponDTO) {
        CouponDTO savedCoupon = couponService.addCoupon(couponDTO);
        return ResponseEntity.ok(savedCoupon);
    }

    // 특정 유저의 쿠폰 조회
    @GetMapping("/user")
    public ResponseEntity<List<CouponDTO>> getCouponsByUserId(@AuthenticationPrincipal CustomUserDetails cud) {
        String userEmail = cud.getUsername();
        User user = userService.getUser(userEmail);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 서비스에서 Coupon 엔터티 목록을 가져오고 DTO로 변환
        List<CouponDTO> couponDTOs = couponService.getCouponsByUserId(user.getId()).stream()
                .map(coupon -> {
                    CouponDTO dto = new CouponDTO();
                    dto.setId(coupon.getId());
                    dto.setCouponName(coupon.getCouponName());
                    dto.setDiscountRate(coupon.getDiscountRate());
                    dto.setIssueDate(coupon.getIssueDate());
                    dto.setUserId(coupon.getUser().getId()); // 최소 정보 설정
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(couponDTOs);
    }

    // 쿠폰 삭제
    @DeleteMapping("/delete/{couponId}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable("couponId") int couponId) {
        couponService.deleteCoupon(couponId);
        return ResponseEntity.noContent().build();
    }
}
