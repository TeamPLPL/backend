package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Funding;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Integer> {

    List<Funding> findTopByOrderByPublishDateDesc(Pageable pageable);

    @Query("SELECT f, COUNT(DISTINCT fs.user.id) as supporterCount " +
            "FROM Funding f " +
            "LEFT JOIN FundingSupport fs ON f.id = fs.funding.id " +
            "WHERE f.fundingStartDate <= :currentDate AND f.fundingEndDate >= :currentDate " +
            "GROUP BY f.id " +
            "ORDER BY supporterCount DESC")
    List<Funding> findTopFundingsWithSupporterCount(Pageable pageable, @Param("currentDate") LocalDateTime currentDate);
}
