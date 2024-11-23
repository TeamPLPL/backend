package com.kosa.backend.funding.support.controller;

import com.kosa.backend.funding.support.dto.FollowDTO;
import com.kosa.backend.funding.support.entity.Follow;
import com.kosa.backend.funding.support.service.FollowService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.repository.MakerRepository;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follow")
public class FollowController {
    private final FollowService followService;
    private final UserService userService;
    private final MakerRepository makerRepository; // MakerRepository 주입

    // 유저별 팔로우 전체 조회
    @GetMapping("/list")
    public ResponseEntity<List<FollowDTO>> getFollows(@AuthenticationPrincipal CustomUserDetails cud) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        List<FollowDTO> follows = followService.getFollowsByUser(user);
        return ResponseEntity.ok(follows);
    }

    // 팔로우 추가
    @PostMapping("/add/{makerId}")
    public ResponseEntity<Void> addFollow(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("makerId") int makerId) {
        var user = CommonUtils.getCurrentUser(cud, userService);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Maker를 데이터베이스에서 조회
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("Maker not found with ID: " + makerId));

        followService.addFollow(user, maker);
        return ResponseEntity.ok().build();
    }

    // 팔로우 삭제
    @DeleteMapping("/remove/{makerId}")
    public ResponseEntity<Void> removeFollow(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable("makerId") int makerId) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        // Maker를 데이터베이스에서 조회
        Maker maker = makerRepository.findById(makerId)
                .orElseThrow(() -> new IllegalArgumentException("Maker not found with ID: " + makerId));

        followService.removeFollow(user, maker);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/latest/{count}")
    public ResponseEntity<List<FollowDTO>> getLatestFollows(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable int count) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FollowDTO> follows = followService.getLatestFollowsByUser(user, count);
        return ResponseEntity.ok(follows);
    }
}