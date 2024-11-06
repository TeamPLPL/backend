package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardInfoRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RewardService {
    private final FundingRepository fundingRepository;
    private final RewardRepository rewardRepository;
    private final RewardInfoRepository rewardInfoRepository;

    // 리워드 생성
    public void save(List<RequestRewardDTO> rewardDTOList, int projectId) {
        

    }
}
