package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.common.entity.Files;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.payment.entity.PaymentHistory;
import com.kosa.backend.user.entity.Maker;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "FUNDING")
public class Funding extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String fundingTitle;

    private int targetAmount;

    private int currentAmount;

    private int complaintCount;

    private LocalDateTime fundingStartDate;

    private LocalDateTime fundingEndDate;

    @Enumerated(EnumType.STRING)
    @Column
    private MakerType makerType;

    private String repName;

    private String repEmail;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String fundingExplanation;

    private String fundingTag;

    private boolean saveStatus;

    private boolean isPublished;

    private LocalDateTime publishDate;

//    @OneToOne(cascade = CascadeType.REMOVE)
//    @JoinColumn(name = "sub_category_id", nullable = true)
//    private SubCategory subCategory;
    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = true)
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
    private List<PaymentHistory> paymentHistory;

    @OneToMany(mappedBy = "funding", cascade = CascadeType.ALL)
    List<Files> files;

    // 작성자 : 신은호, 작성 내용 : 리워드 정책, 펀딩 1:1 관계
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "funding")
    private RewardInfo rewardInfo;

    // fundingTitle 업데이트 메서드
    public void updateFundingTitle(String fundingTitle) {
        this.fundingTitle = fundingTitle;
    }

    // 작성자 : 신은호, 내용 : 펀딩 프로젝트 업데이트 메서드, 추후 리펙토링 예정,,,
    // targetAmount 업데이트 메서드
    public void updateTargetAmount(int targetAmount) {
        this.targetAmount = targetAmount;
    }

    // fundingStartDate 업데이트 메서드
    public void updateFundingStartDate(LocalDateTime fundingStartDate) {
        this.fundingStartDate = fundingStartDate;
    }

    // fundingEndDate 업데이트 메서드
    public void updateFundingEndDate(LocalDateTime fundingEndDate) {
        this.fundingEndDate = fundingEndDate;
    }

    // makerType 업데이트 메서드
    public void updateMakerType(MakerType makerType) {
        this.makerType = makerType;
    }

    // repName 업데이트 메서드
    public void updateRepName(String repName) {
        this.repName = repName;
    }

    // repEmail 업데이트 메서드
    public void updateRepEmail(String repEmail) {
        this.repEmail = repEmail;
    }

    // fundingExplanation 업데이트 메서드
    public void updateFundingExplanation(String fundingExplanation) {
        this.fundingExplanation = fundingExplanation;
    }

    // fundingTag 업데이트 메서드
    public void updateFundingTag(String fundingTag) {
        this.fundingTag = fundingTag;
    }

    // subCategory 업데이트 메서드
    public void updateSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    // personalMaker 업데이트 메서드
    public void updatePersoanlMaker(PersonalMaker personalMaker) {
        this.personalMaker = personalMaker;
    }

    // businessMaker 업데이트 메서드
    public void updateBusinessMaker(BusinessMaker businessMaker) {
        this.businessMaker = businessMaker;
    }

    public void updateRewardInfo(RewardInfo rewardInfo) {
        this.rewardInfo = rewardInfo;
    }

    public void updateCurrentAmount(int currentAmount) {this.currentAmount = currentAmount;}

    public void updateIsPublished(boolean isPublished) {this.isPublished = isPublished;}

    public void updatePublishDate(LocalDateTime publishDate) {this.publishDate = publishDate;}

    // getter 명시적 생성
    public boolean getSaveStatus() {
        return saveStatus;
    }
}
