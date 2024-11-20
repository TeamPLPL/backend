package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.BusinessMaker;
import com.kosa.backend.funding.project.entity.Funding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessMakerRepository extends JpaRepository<BusinessMaker, Integer> {
    Optional<BusinessMaker> findByFunding(Funding funding);
}
