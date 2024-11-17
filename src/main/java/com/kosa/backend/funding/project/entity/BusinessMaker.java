package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.dto.BusinessMakerDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @Column(nullable = true)
    private String businessRegistNum;

    @Column(nullable = true)
    private String businessRegistCertif;

    @Column(nullable = true)
    private String companyName;

    @OneToOne(mappedBy = "businessMaker", cascade = CascadeType.ALL)
    private Funding funding;

    @Builder
    public BusinessMaker(String businessRegistNum, String businessRegistCertif, String companyName) {
        this.businessRegistNum = businessRegistNum;
        this.businessRegistCertif = businessRegistCertif;
        this.companyName = companyName;
    }

    public void updateFromDTO(BusinessMakerDTO dto) {
        if (dto.getBusinessRegistNum() != null) {
            this.businessRegistNum = dto.getBusinessRegistNum();
        }
        if (dto.getBusinessRegistCertif() != null) {
            this.businessRegistCertif = dto.getBusinessRegistCertif();
        }
        if (dto.getCompanyName() != null) {
            this.companyName = dto.getCompanyName();
        }
    }
}
