package com.kosa.backend.funding.support.service;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.support.entity.Wishlist;
import com.kosa.backend.funding.support.repository.WishlistRepository;
import com.kosa.backend.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final FundingRepository fundingRepository;

    public List<Integer> getWishlistsByUser(int userId) {
        return wishlistRepository.findFundingIdsByUserId(userId);
    }

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
            Funding funding = fundingRepository.findById(fundingId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid funding ID: " + fundingId));
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setFunding(funding);
            wishlistRepository.save(wishlist);
        }

        // 5. 제거 수행
        for (Integer fundingId : toRemove) {
            wishlistRepository.deleteByUserIdAndFundingId(user.getId(), fundingId);
        }
    }
}
