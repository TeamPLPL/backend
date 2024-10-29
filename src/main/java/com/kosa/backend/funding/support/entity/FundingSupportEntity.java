package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.FundingEntity;
import com.kosa.backend.funding.project.entity.RewardEntity;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING_SUPPORT")
public class FundingSupportEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int rewardCount;

    @Column(nullable = false)
    private LocalDateTime supportDate;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private FundingEntity funding;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "reward_id", nullable = false)
    private RewardEntity reward;
}
