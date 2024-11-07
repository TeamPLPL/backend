package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.entity.Funding;
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
    public int save(List<RequestRewardDTO> rewardDTOList, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. Reward Lists 저장
        for(RequestRewardDTO rewardDTO : rewardDTOList) {
            rewardRepository.save(Reward.toSaveEntity(rewardDTO, funding));
        }

        // 3. 저장
        return fundingRepository.save(funding).getId();
    }

    // 리워드 업데이트
    public int update(List<RequestRewardDTO> rewardDTOList, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. Reward Lists 업데이트
        for(RequestRewardDTO rewardDTO : rewardDTOList) {
            rewardRepository.save(Reward.toSaveEntity(rewardDTO, funding));
        }

        // 3. 저장
        return fundingRepository.save(funding).getId();
    }
}
