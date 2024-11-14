package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Integer> {
    Optional<Reward> findById(int rewardId);
    List<Reward> findAllByFundingId(int fundingId);
}
