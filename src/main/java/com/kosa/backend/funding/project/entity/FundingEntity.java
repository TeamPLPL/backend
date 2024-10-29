package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.AuditableEntity;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.user.entity.MakerEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING")
public class FundingEntity extends AuditableEntity {
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

    @Column(nullable = false)
    private String fundingExplanation;

    @Column(nullable = false)
    private String fundingTag;

    @Column(nullable = false)
    private boolean saveStatus;

    @Column(nullable = false)
    private boolean isPublished;

    private LocalDateTime publishDate;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategoryEntity subCategory;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = false)
    private MakerEntity maker;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "business_maker_id", nullable = true)
    private BusinessMakerEntity businessMaker;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "personal_maker_id", nullable = true)
    private PersonalMakerEntity personalMaker;

    @OneToMany(mappedBy = "reward", cascade = CascadeType.ALL)
    List<RewardEntity> rewards;

    @OneToMany(mappedBy = "reward_info", cascade = CascadeType.ALL)
    List<RewardInfoEntity> rewardInfos;
}