package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.FundingSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingSupportRepository extends JpaRepository<FundingSupport, Integer> {
    List<FundingSupport> findByFundingId(int fundingId);
    List<FundingSupport> findAllByRewardId(int rewardId);

    List<FundingSupport> findByFundingIdAndUserIdAndPaymentId(int fundingId, int userId, int paymentId);
    void deleteByPaymentId(int paymentId);  // 새로운 삭제 메서드

    int countByFundingId(int fundingId);

    @Query("SELECT COUNT(DISTINCT fs.user.id) FROM FundingSupport fs WHERE fs.funding.id = :fundingId")
    int countDistinctUsersByFundingId(@Param("fundingId") int fundingId);

}
