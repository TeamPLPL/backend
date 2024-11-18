package com.kosa.backend.funding.support.service;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.support.entity.Wishlist;
import com.kosa.backend.funding.support.repository.WishlistRepository;
import com.kosa.backend.user.entity.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final FundingRepository fundingRepository;

    // User의 찜 리스트 전체 조회
    public List<Integer> getWishlistsByUser(int userId) {
        return wishlistRepository.findFundingIdsByUserId(userId);
    }

    // 찜 목록 전체 업데이트
    @Transactional
    public void updateWishlist(User user, List<Integer> newWishlist) {
        List<Integer> currentWishlist = wishlistRepository.findFundingIdsByUserId(user.getId());

        List<Integer> toAdd = newWishlist.stream()
                .filter(fundingId -> !currentWishlist.contains(fundingId))
                .toList();

        List<Integer> toRemove = currentWishlist.stream()
                .filter(fundingId -> !newWishlist.contains(fundingId))
                .toList();

        for (Integer fundingId : toAdd) {
            addToWishlist(user, fundingId);
        }

        for (Integer fundingId : toRemove) {
            removeFromWishlist(user, fundingId);
        }
    }

    // 찜 추가
    public void addToWishlist(User user, int fundingId) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid funding ID: " + fundingId));
        Wishlist wl = setNewWishlist(user, funding);
        wishlistRepository.save(wl);
    }

    // 새 Wishlist 타입 객체 세팅
    public Wishlist setNewWishlist(User user, Funding funding) {
        Wishlist wl = new Wishlist();
        wl.setUser(user);
        wl.setFunding(funding);
        return wl;
    }

    // 찜 삭제
    @Transactional
    public void removeFromWishlist(User user, int fundingId) {
        wishlistRepository.deleteByUserIdAndFundingId(user.getId(), fundingId);
    }

    // 찜 여부 체크
    public boolean isWishlist(int userId, int fundingId) {
        return wishlistRepository.existsByUserIdAndFundingId(userId, fundingId);
    }

    // 페이징된 찜리스트 조회
    @Transactional(readOnly = true)
    public Page<Integer> getPagedWishlistsByUser(int userId, Pageable pageable) {
        return wishlistRepository.findFundingIdsByUserId(userId, pageable);
    }
}
