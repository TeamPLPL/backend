package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.user.entity.Maker;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING")
public class Funding extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String fundingTitle;

    @Column(nullable = false)
    private int targetAmount;

    @Column(nullable = false)
    private int currentAmount;

    @Column(nullable = false)
    private int complaintCount;

    @Column(nullable = false)
    private LocalDateTime fundingStartDate;

    @Column(nullable = false)
    private LocalDateTime fundingEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MakerType makerType;

    @Column(nullable = false)
    private String repName;

    @Column(nullable = false)
    private String repEmail;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String fundingExplanation;

    @Column(nullable = false)
    private String fundingTag;

    @Column(nullable = false)
    private boolean saveStatus;

    @Column(nullable = false)
    private boolean isPublished;

    private LocalDateTime publishDate;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sub_category_id", nullable = true)
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = false)
    private Maker maker;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "business_maker_id", nullable = true)
    private BusinessMaker businessMaker;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "personal_maker_id", nullable = true)
    private PersonalMaker personalMaker;

    @OneToMany(mappedBy = "funding", cascade = CascadeType.ALL)
    List<Reward> rewards;

    @ManyToOne
    @JoinColumn(name = "funding_support_id", nullable = true)
    private FundingSupport fundingSupport;

    @OneToMany(mappedBy = "funding", cascade = CascadeType.ALL)
    List<Files> files;
}
