package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
    // JpaRepository provides basic CRUD method.
    List<Coupon> findByUser_Id(int userId);  // 특정 유저의 쿠폰 목록을 조회하는 메서드

    // 특정 유저의 사용되지 않은 쿠폰 개수 조회
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.user.id = :userId AND c.id NOT IN (SELECT p.coupon.id FROM Payment p WHERE p.coupon IS NOT NULL)")
    int countUnusedCouponsByUserId(@Param("userId") int userId);
}
