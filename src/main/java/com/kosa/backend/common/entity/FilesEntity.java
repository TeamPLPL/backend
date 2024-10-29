package com.kosa.backend.common.entity;

import com.kosa.backend.common.entity.enums.ImgType;
import com.kosa.backend.funding.project.entity.FundingEntity;
import com.kosa.backend.user.entity.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "FILES")
public class FilesEntity extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String originalNm;

    @Column(nullable = false)
    private String savedNm;

    @Enumerated(EnumType.STRING)
    private ImgType imgType;

    @ManyToOne
    @JoinColumn(name = "funding_id", nullable = false)
    private FundingEntity funding;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
