package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    boolean existsByFollowedUserIdAndFollowingUserId(int makerId, int userId);
}
