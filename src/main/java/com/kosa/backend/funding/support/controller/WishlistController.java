package com.kosa.backend.funding.support.controller;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.funding.support.dto.WishlistDTO;
import com.kosa.backend.funding.support.service.WishlistService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wish")
public class WishlistController {
    private final UserService userService;
    private final WishlistService wishlistService;

    // 찜리스트 조회
    @GetMapping("/list")
    public ResponseEntity<List<WishlistDTO>> getWishlists(@AuthenticationPrincipal CustomUserDetails cud) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
        List<WishlistDTO> allWishlists = wishlistService.getWishlistsByUser(user.getId());
        return ResponseEntity.ok(allWishlists);
    }

    // 찜리스트 업데이트
    @PostMapping("/update")
    public ResponseEntity<Void> updateWishlist(@AuthenticationPrincipal CustomUserDetails cud, @RequestBody List<Integer> wishlist) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
        wishlistService.updateWishlist(user, wishlist);
        return ResponseEntity.ok().build();
    }

    // fundingId로 찜 추가
    @PostMapping("/add/{id}")
    public ResponseEntity<Void> addToWishlist(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable(name = "id") Integer fundingId) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        try {
            wishlistService.addToWishlist(user, fundingId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // fundingId로 찜 삭제
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> removeFromWishlist(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable(name = "id") Integer fundingId) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        wishlistService.removeFromWishlist(user, fundingId);
        return ResponseEntity.ok().build();
    }

    // 찜 여부 체크
    @GetMapping("/check/{id}")
    public ResponseEntity<Boolean> checkWishlistStatus(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable(name = "id") Integer fundingId) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        boolean isInWishlist = wishlistService.isWishlist(user.getId(), fundingId);
        return ResponseEntity.ok(isInWishlist);
    }

    // 페이징된 찜 리스트 조회
    @GetMapping("/list/paged")
    public ResponseEntity<Page<WishlistDTO>> getPagedWishlists(
            @AuthenticationPrincipal CustomUserDetails cud,
            @PageableDefault(size = Const.DEFAULT_PAGE_SIZE) Pageable pageable) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Page<WishlistDTO> pagedWishlists = wishlistService.getPagedWishlistsByUser(user.getId(), pageable);
        if (pagedWishlists.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(pagedWishlists);
    }

}