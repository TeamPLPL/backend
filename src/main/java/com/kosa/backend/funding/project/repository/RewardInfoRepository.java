package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.Reward;
import com.kosa.backend.funding.project.entity.RewardInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardInfoRepository extends JpaRepository<RewardInfo, Integer> {
    List<RewardInfo> findByFunding(Funding funding);
}
