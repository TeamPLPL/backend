package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.FundingEntity;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SUPPORTER_BOARD")
public class SupporterBoardEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardCategory boardCategory;

    @Column(nullable = false)
    private LocalDateTime boardDate;

    @Column(nullable = false, length = 8000)
    private String boardContent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private FundingEntity funding;

    // Enum for board category
    public enum BoardCategory {
        QUESTION, REVIEW, DISCUSSION
    }

    // getters and setters
}