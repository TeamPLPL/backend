package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "SUPPORTER_BOARD")
public class SupporterBoard extends Auditable {
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    // Enum for board category
    public enum BoardCategory {
        QUESTION, REVIEW, DISCUSSION
    }

    // getters and setters
}