package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.support.entity.enums.ComplaintCategory;
import com.kosa.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "COMPLAINT")
public class Complaint extends Auditable {
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
    private User user;

    @ManyToOne
    @JoinColumn(name = "complaint_user_id")
    private User complaintUser;

    @ManyToOne
    @JoinColumn(name = "funding_id")
    private Funding funding;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private SupporterBoard board;
}
