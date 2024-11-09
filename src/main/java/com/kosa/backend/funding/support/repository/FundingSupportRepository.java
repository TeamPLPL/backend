package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.FundingSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingSupportRepository extends JpaRepository<FundingSupport, Integer> {
    List<FundingSupport> findByFundingId(int fundingId);
    List<FundingSupport> findAllByRewardId(int rewardId);
}
