package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.FundingEntity;
import com.kosa.backend.funding.support.entity.enums.ComplaintCategory;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COMPLAINT")
public class ComplaintEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory complaintCategory;

    @Column(nullable = false)
    private String complaintTitle;

    @Column(nullable = false)
    private LocalDateTime complaintDate;

    @Column(nullable = false)
    private String complaintContent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "complaint_user_id")
    private UserEntity complaintUser;

    @ManyToOne
    @JoinColumn(name = "funding_id")
    private FundingEntity funding;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private SupporterBoardEntity board;
}
