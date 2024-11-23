package com.kosa.backend.funding.support.service;

import com.kosa.backend.funding.support.entity.Follow;
import com.kosa.backend.funding.support.repository.FollowRepository;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;

    // 유저별 팔로우 전체 조회
    public List<Follow> getFollowsByUser(User user) {
        return followRepository.findAllByFollowingUser(user);
    }

    // 팔로우 추가
    @Transactional
    public Follow addFollow(User followingUser, Maker followedUser) {
        if (followRepository.existsByFollowedUserIdAndFollowingUserId(followedUser.getId(), followingUser.getId())) {
            throw new IllegalStateException("Already following this user.");
        }

        // Builder로 Follow 엔터티 생성
        Follow follow = Follow.builder()
                .followedUser(followedUser)
                .followingUser(followingUser)
                .build();

        return followRepository.save(follow);
    }

    // 팔로우 삭제
    @Transactional
    public void removeFollow(User followingUser, Maker followedUser) {
        Follow follow = followRepository.findByFollowedUserAndFollowingUser(followedUser, followingUser)
                .orElseThrow(() -> new IllegalArgumentException("Follow relationship does not exist."));
        followRepository.delete(follow);
    }

    // 최신 Follow 3개 조회
    public List<Follow> getLatestFollowsByUser(User user, int count) {
        Pageable pageable = Pageable.ofSize(count).withPage(0); // 최신 count개 페이징
        return followRepository.findAllByFollowingUser(user, pageable).getContent();
    }

}