package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.payment.entity.Payment;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING_SUPPORT")
public class FundingSupport extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int rewardCount;

    @Column(nullable = false)
    private LocalDateTime supportDate;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private Reward reward;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)  // Payment와의 관계 추가
    private Payment payment;

    @Builder
    public FundingSupport(Funding funding, User user, Reward reward, Payment payment, int rewardCount, LocalDateTime supportDate) {
        this.funding = funding;
        this.user = user;
        this.reward = reward;
        this.payment = payment;
        this.rewardCount = rewardCount;
        this.supportDate = supportDate;
    }
}
