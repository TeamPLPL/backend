package com.kosa.backend.funding.project.dto;

import com.kosa.backend.funding.project.entity.PersonalMaker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalMakerDTO {
    private int id;
    private String identityCard;

    // 엔티티 -> DTO 변환 메서드
    public static PersonalMakerDTO fromEntity(PersonalMaker personalMaker) {
        if (personalMaker == null) {
            // Null 객체일 경우 null을 반환하거나 예외를 던짐
            return null;
        }

        return PersonalMakerDTO.builder()
                .id(personalMaker.getId())
                .identityCard(personalMaker.getIdentityCard())
                .build();
    }

}
