package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    // JpaRepository provides basic CRUD method.
}
