package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.RewardDTO;
import com.kosa.backend.funding.project.dto.RewardResponseDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardDTO;
import com.kosa.backend.funding.project.dto.requestdto.RequestRewardInfoDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.entity.RewardInfo;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.RewardInfoRepository;
import com.kosa.backend.funding.project.repository.RewardRepository;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RewardService {
    private final FundingRepository fundingRepository;
    private final RewardRepository rewardRepository;
    private final RewardInfoRepository rewardInfoRepository;
    private final FundingSupportRepository fundingSupportRepository;

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

    // 리워드 정책 생성
    public int savePolicy(RequestRewardInfoDTO rewardInfoDTO, int projectId) {
        // 1. 기존 Funding 객체 조회
        Funding funding = fundingRepository.findById(projectId).orElseThrow(() ->
                new IllegalArgumentException("해당 프로젝트를 찾을 수 없습니다. ID: " + projectId));

        // 2. 리워드 정책 저장
        rewardInfoRepository.save(RewardInfo.toSaveEntity(rewardInfoDTO, funding));

        // 3. 저장
        return fundingRepository.save(funding).getId();
    }

    // 구매할 reward들에 대한 정보 담은 rewardDTOList와 가방 비싼 배송비 하나를 반환하는 메소드
    public ResponseEntity<RewardResponseDTO> getRewardDTOList(List<RewardDTO> rewardDTOList) {
        int deliveryFee = 0;

        List<RewardDTO> rDTOList = new ArrayList<>();
        for(RewardDTO rewardDTO : rewardDTOList) {
            Reward reward = rewardRepository.findById(rewardDTO.getRewardId()).orElseThrow(() -> new EntityNotFoundException("해당 리워드를 찾을 수 없습니다. ID: " + rewardDTO.getRewardId()));

            if(isOverQuantityLimit(reward.getId(), reward.getQuantityLimit())) {
                throw new IllegalArgumentException("해당 리워드의 구매 수량 제한을 초과합니다. reward ID: " + reward.getId());
            }

            RewardDTO rDTO = RewardDTO.builder()
                    .rewardId(reward.getId())
                    .rewardName(reward.getRewardName())
                    .price(reward.getPrice())
                    .deliveryStartDate(reward.getDeliveryStartDate())
                    .count(rewardDTO.getCount())
                    .build();
            rDTOList.add(rDTO);

            // 배송비 계산
            if(deliveryFee < reward.getDeliveryFee()) { deliveryFee = reward.getDeliveryFee(); }
        }

        RewardResponseDTO rewardResponseDTO = RewardResponseDTO.builder()
                .rewardDTOList(rDTOList)
                .deliveryFee(deliveryFee)
                .build();
        return ResponseEntity.ok(rewardResponseDTO);
    }

    // reward의 quantityLimit과 후원된 개수를 체크하는 메소드
    // reward의 quantityLimit이 후원된 리워드 전체 개수보다 작을 때 true값 반환
    public boolean isOverQuantityLimit(int rewardId, int quantityLimit) {
        List<FundingSupport> fundingSupportList = fundingSupportRepository.findAllByRewardId(rewardId);
        if(!fundingSupportList.isEmpty() && quantityLimit>0) {
            int totalCnt = 0;
            for(FundingSupport fs : fundingSupportList) {
                totalCnt += fs.getRewardCount();
            }
            return quantityLimit < totalCnt;
        }
        return false;
    }

    // 펀딩Id별 리워드 전체 리스트 반환하는 메소드
    public ResponseEntity<List<RewardDTO>> getAllRewardDTOList(int fundingId) {
        List<Reward> rewardList = rewardRepository.findAllByFundingId(fundingId);
        if(rewardList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // 후원 개수 구하기
        int supportedCnt = fundingSupportRepository.countByFundingId(fundingId);

        List<RewardDTO> rewardDTOList = new ArrayList<>();
        for(Reward reward : rewardList) {
            RewardDTO rDTO = RewardDTO.builder()
                    .rewardId(reward.getId())
                    .rewardName(reward.getRewardName())
                    .price(reward.getPrice())
                    .explanation(reward.getExplanation())
                    .deliveryStartDate(reward.getDeliveryStartDate())
                    .deliveryFee(reward.getDeliveryFee())
                    .supportedCnt(supportedCnt)
                    .build();
            rewardDTOList.add(rDTO);
        }

        return ResponseEntity.ok(rewardDTOList);
    }
}
