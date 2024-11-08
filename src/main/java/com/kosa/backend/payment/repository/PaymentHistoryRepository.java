package com.kosa.backend.payment.repository;

import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {
    Optional<PaymentHistory> findByPayment(Payment payment);
    void deleteByPayment(Payment payment);

}
