package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.SupporterBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupporterBoardRepository extends JpaRepository<SupporterBoard, Integer> {
    List<SupporterBoard> findByFundingId(int fundingId);
}
