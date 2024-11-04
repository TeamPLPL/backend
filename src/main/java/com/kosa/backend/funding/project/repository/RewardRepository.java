package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RewardRepository extends JpaRepository<Reward, Integer> {
}
