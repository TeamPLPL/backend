package com.kosa.backend.payment.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT_METHOD")
public class PaymentMethodEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String methodType;

    @Column(nullable = false)
    private String cardNumber;

    private String thirdPartyId;

    private String thirdPartyPw;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private PaymentEntity payment;
}
