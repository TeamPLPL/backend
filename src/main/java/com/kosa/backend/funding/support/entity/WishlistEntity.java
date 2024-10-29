package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.FundingEntity;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "WISHLIST")
public class WishlistEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private FundingEntity funding;

    // getters and setters
}
