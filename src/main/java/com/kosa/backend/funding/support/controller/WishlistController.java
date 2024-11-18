package com.kosa.backend.funding.support.controller;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.service.FundingService;
import com.kosa.backend.funding.support.service.WishlistService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
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
@RequestMapping("/api/wish")
public class WishlistController {
    private final UserService userService;
    private final WishlistService wishlistService;

    @GetMapping("/list")
    public ResponseEntity<List<Integer>> getWishlists(@AuthenticationPrincipal CustomUserDetails cud) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
        List<Integer> wishlists = wishlistService.getWishlistsByUser(user.getId());
        return ResponseEntity.ok(wishlists);
    }

    @PostMapping("/update")
    public ResponseEntity<Void> updateWishlist(@AuthenticationPrincipal CustomUserDetails cud, @RequestBody List<Integer> wishlist) {
        User user = CommonUtils.getCurrentUser(cud, userService);
        if(user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
        wishlistService.updateWishlist(user, wishlist);
        return ResponseEntity.ok().build();
    }
}
