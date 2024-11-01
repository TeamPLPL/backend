package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    // JpaRepository provides basic CRUD method.
}
