package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.requestdto.RequestProjectDTO;
import com.kosa.backend.funding.project.entity.enums.MakerType;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.user.entity.Maker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final FundingRepository fundingRepository;

    public void save(RequestProjectDTO projectDTO, Maker maker) {
        fundingRepository.save(RequestProjectDTO.toSaveEntity(maker));
    }


}
