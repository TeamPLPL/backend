package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // JpaRepository provides basic CRUD method.
    List<Payment> findByUser(User user);  // 특정 유저의 결제 목록을 조회하는 메서드
}
