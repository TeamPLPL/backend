package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RewardRepository extends JpaRepository<Reward, Integer> {
    Optional<Reward> findById(int rewardId);
    List<Reward> findAllByFundingId(int fundingId);

    // 작성자 신은호
    @Modifying // 데이터베이스에 영향을 미치는 쿼리임을 명시
    @Query("DELETE FROM Reward r WHERE r.funding.id = :fundingId")
    void deleteByFundingId(@Param("fundingId") int fundingId);
}
