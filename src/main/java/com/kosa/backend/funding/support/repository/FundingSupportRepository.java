package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.FundingSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingSupportRepository extends JpaRepository<FundingSupport, Integer> {
    List<FundingSupport> findByFundingId(int fundingId);
    List<FundingSupport> findAllByRewardId(int rewardId);

    List<FundingSupport> findByFundingIdAndUserIdAndPaymentId(int fundingId, int userId, int paymentId);

    // 새로운 메서드: paymentId 기반 조회
    List<FundingSupport> findByPaymentId(int paymentId);

    void deleteByPaymentId(int paymentId);  // 새로운 삭제 메서드

    int countByFundingId(int fundingId);
}
