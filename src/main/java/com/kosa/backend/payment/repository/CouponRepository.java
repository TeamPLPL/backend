package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    // JpaRepository provides basic CRUD method.
    List<Coupon> findByUser_Id(int userId);  // 특정 유저의 쿠폰 목록을 조회하는 메서드
}
