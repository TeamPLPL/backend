package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.entity.RewardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardInfoRepository extends JpaRepository<RewardInfo, Integer> {
    RewardInfo findByFundingId(int fundingId);
}
