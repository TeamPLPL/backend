package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    // JpaRepository provides basic CRUD method.
    void deleteByPaymentAndPaymentUserId(Payment payment, int userId);
}
