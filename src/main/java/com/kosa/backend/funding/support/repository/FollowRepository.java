package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.support.entity.Follow;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Integer> {
    boolean existsByFollowedUserIdAndFollowingUserId(int makerId, int userId);

    // 특정 유저가 팔로우 중인 목록 조회
    List<Follow> findAllByFollowingUser(User followingUser);

    // 특정 Maker와 User 관계 조회
    Optional<Follow> findByFollowedUserAndFollowingUser(Maker followedUser, User followingUser);

    // 최신 Follow 데이터 페이징 조회
    Page<Follow> findAllByFollowingUser(User followingUser, Pageable pageable);
}