package com.kosa.backend.payment.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.payment.entity.eunms.PaymentStatus;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENT")
public class PaymentEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private String deliveryAddress;

    private String phoneNum;

    @Column(nullable = false)
    private String receiverName;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponEntity coupon;
}
