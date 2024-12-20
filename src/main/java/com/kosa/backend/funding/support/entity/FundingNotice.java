package com.kosa.backend.funding.support.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.entity.Funding;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "FUNDING_NOTICE")
public class FundingNotice extends Auditable {
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
    private Funding funding;
}
