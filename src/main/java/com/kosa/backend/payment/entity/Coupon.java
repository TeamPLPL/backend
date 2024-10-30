package com.kosa.backend.payment.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "COUPON")
public class Coupon extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // Assuming coupon_id is a String type

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false)
    private int discountRate;

    @Column(nullable = false)
    private LocalDateTime issueDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
