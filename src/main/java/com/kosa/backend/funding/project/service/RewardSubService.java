package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.funding.project.dto.RewardInfoDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardInfoDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.entity.RewardInfo;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardInfoRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RewardSubService {
    // 작성자 : 신은호, 작성 내용 : 작성한 RewardService 분리
    private final FundingRepository fundingRepository;
    private final RewardRepository rewardRepository;
    private final RewardInfoRepository rewardInfoRepository;

    // 리워드 생성
    public int save(List<RequestRewardDTO> rewardDTOList, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. Reward Lists 저장
        for (RequestRewardDTO rewardDTO : rewardDTOList) {
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
        for (RequestRewardDTO rewardDTO : rewardDTOList) {
            rewardRepository.save(Reward.toSaveEntity(rewardDTO, funding));
        }

        // 3. 저장
        return fundingRepository.save(funding).getId();
    }

    @Transactional
    public int savePolicy(RequestRewardInfoDTO rewardInfoDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        RewardInfo rewardInfo = Optional.ofNullable(rewardInfoRepository.findByFundingId(funding.getId()))
                .orElseGet(() -> RewardInfo.builder()
                        .funding(funding)
                        .build());

        // 3. 리워드 정책 업데이트
        updateRewardInfo(rewardInfo, rewardInfoDTO);

        // 4. 저장
        rewardInfoRepository.save(rewardInfo);

        return funding.getId();
    }

    // 리워드 정책 업데이트 메서드
    private void updateRewardInfo(RewardInfo rewardInfo, RequestRewardInfoDTO rewardInfoDTO) {
        rewardInfo.updateModelName(rewardInfoDTO.getModelName());
        rewardInfo.updateProductMaterial(rewardInfoDTO.getProductMaterial());
        rewardInfo.updateColor(rewardInfoDTO.getColor());
        rewardInfo.updateField(rewardInfoDTO.getField());
        rewardInfo.updateManufacturer(rewardInfoDTO.getManufacturer());
        rewardInfo.updateManufacturingCountry(rewardInfoDTO.getManufacturingCountry());
        rewardInfo.updateManufactureDate(rewardInfoDTO.getManufactureDate());
    }


    // 리워드 리스트 가져오는 서비스 로직
    public List<RewardDTO> findAll(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        List<Reward> rewardList = rewardRepository.findAllByFundingId(funding.getId());
        List<RewardDTO> rewardDTOList = new ArrayList<>();

        for (Reward reward : rewardList) {
            rewardDTOList.add(RewardDTO.fromEntity(reward));
        }

        return rewardDTOList;
    }

    // 리워드 정책 가져오는 서비스 로직
    public RewardInfoDTO findRewardInfo(int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. RewardInfo 객체 조회
        RewardInfo rewardInfo = rewardInfoRepository.findByFundingId(funding.getId());

        // 3. null인 경우 기본값 반환
        if (rewardInfo == null) {
            return new RewardInfoDTO(); // 기본 DTO 반환
        }

        // 4. DTO로 변환 및 반환
        return RewardInfoDTO.fromEntity(rewardInfo);
    }

    // 리워드 삭제하는 서비스 로직
    public boolean delete(int rewardId) {
        if (rewardRepository.existsById(rewardId)) {
            rewardRepository.deleteById(rewardId);
            return true; // 삭제 성공
        }
        return false; // 삭제 대상 없음
    }

}
