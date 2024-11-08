package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import com.kosa.backend.funding.project.dto.PersonalMakerDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "PERSONAL_MAKER")
public class PersonalMaker extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String identityCard;

    @OneToOne(mappedBy = "personalMaker", cascade = CascadeType.ALL)
    private Funding funding;

    @Builder
    public PersonalMaker(String identityCard) {
        this.identityCard = identityCard;
    }

    // Method to update fields using DTO
    public void updateFromDTO(PersonalMakerDTO dto) {
        if (dto.getIdentityCard() != null) {
            this.identityCard = dto.getIdentityCard();
        }
    }
}