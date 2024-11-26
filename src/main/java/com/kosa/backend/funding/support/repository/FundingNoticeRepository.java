package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.FundingNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingNoticeRepository extends JpaRepository<FundingNotice, Integer> {
    List<FundingNotice> findByFundingIdOrderByUpdatedAtDesc(int fundingId);
}
