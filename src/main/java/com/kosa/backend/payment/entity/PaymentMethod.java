package com.kosa.backend.payment.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PAYMENT_METHOD")
public class PaymentMethod extends Auditable {
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
    private Payment payment;
}
