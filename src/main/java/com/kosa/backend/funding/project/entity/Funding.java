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
@Setter
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

    @Column(nullable = true)
    private String fundingTitle;

    @Column(nullable = true)
    private int targetAmount;

    @Column(nullable = true)
    private int currentAmount;

    @Column(nullable = true)
    private int complaintCount;

    @Column(nullable = true)
    private LocalDateTime fundingStartDate;

    @Column(nullable = true)
    private LocalDateTime fundingEndDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MakerType makerType;

    @Column(nullable = true)
    private String repName;

    @Column(nullable = true)
    private String repEmail;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String fundingExplanation;

    @Column(nullable = true)
    private String fundingTag;

    @Column(nullable = true)
    private boolean saveStatus;

    @Column(nullable = true)
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
}
