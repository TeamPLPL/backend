package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "BUSINESS_MAKER")
public class BusinessMaker extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String businessRegistNum;

    @Column(nullable = false)
    private String businessRegistCertif;

    @Column(nullable = false)
    private String companyName;
}
