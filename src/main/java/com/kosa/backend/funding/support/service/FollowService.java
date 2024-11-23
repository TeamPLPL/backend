package com.kosa.backend.funding.support.service;

import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.support.dto.FollowDTO;
import com.kosa.backend.funding.support.entity.Follow;
import com.kosa.backend.funding.support.repository.FollowRepository;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final S3Service s3Service;

    // 유저별 팔로우 전체 조회
    public List<FollowDTO> getFollowsByUser(User user) {
        List<Follow> follows = followRepository.findAllByFollowingUser(user);

        return follows.stream()
                .map(f -> {
                    Maker maker = f.getFollowedUser();
                    String profileImgUrl = null;

                    try {
                        profileImgUrl = s3Service.getProfileImgByUserId(maker.getUser().getId()).getSignedUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return FollowDTO.builder()
                            .id(f.getId())
                            .name(maker.getUser().getUserNick()) // 닉네임
                            .avatar(profileImgUrl) // 프로필 이미지
                            .description(maker.getUserContent()) // 설명
                            .build();
                })
                .collect(Collectors.toList());
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
    public List<FollowDTO> getLatestFollowsByUser(User user, int count) {
        Pageable pageable = Pageable.ofSize(count).withPage(0);
        List<Follow> follows = followRepository.findAllByFollowingUser(user, pageable).getContent();

        return follows.stream()
                .map(f -> {
                    Maker maker = f.getFollowedUser();
                    String profileImgUrl = null;

                    try {
                        profileImgUrl = s3Service.getProfileImgByUserId(maker.getUser().getId()).getSignedUrl();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return FollowDTO.builder()
                            .id(f.getId())
                            .name(maker.getUser().getUserNick())
                            .avatar(profileImgUrl)
                            .description(maker.getUserContent())
                            .build();
                })
                .collect(Collectors.toList());
    }

}