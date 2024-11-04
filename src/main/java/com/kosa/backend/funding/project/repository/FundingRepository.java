package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Funding;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Integer> {

    List<Funding> findTopByOrderByPublishDateDesc(Pageable pageable);
//    List<Funding>  findTopNByOrderByPublishDateDateDesc(int n);
}
