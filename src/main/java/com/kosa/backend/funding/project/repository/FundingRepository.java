package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingRepository extends JpaRepository<Funding, Integer> {
}
