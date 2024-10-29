package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.FundingEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING_NOTICE")
public class FundingNoticeEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "notice_category_id", nullable = false)
    private int noticeCategoryId;  // 기존 TINYINT 필드에 맞게 정의

    @Column(nullable = false)
    private String noticeTitle;

    @Column(nullable = false)
    private LocalDateTime noticeDate;

    @Column(nullable = false)
    private String noticeContent;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private FundingEntity funding;
}
