package com.kosa.backend.funding.support.service;

import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.support.dto.WishlistDTO;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final FundingRepository fundingRepository;
    private final S3Service s3Service; // S3 서비스 추가

    // User의 찜 리스트 전체 조회
    public List<WishlistDTO> getWishlistsByUser(int userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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

        // 작성자 여부 확인
        if (funding.getMaker().getUser().getId() == user.getId()) {
            throw new IllegalArgumentException("자신이 만든 펀딩은 위시리스트에 추가할 수 없습니다.");
        }

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
    public Page<WishlistDTO> getPagedWishlistsByUser(int userId, Pageable pageable) {
        return wishlistRepository.findByUserId(userId, pageable)
                .map(this::convertToDTO);
    }

    // Wishlist 엔터티를 DTO로 변환
    private WishlistDTO convertToDTO(Wishlist wishlist) {
        Funding funding = wishlist.getFunding();
        String thumbnailUrl = null;

        // S3에서 썸네일 URL 가져오기
        try {
            thumbnailUrl = s3Service.getThumbnailByFundingId(funding.getId()).getSignedUrl();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return WishlistDTO.builder()
                .wishlistId(wishlist.getId())
                .fundingTitle(funding.getFundingTitle())
                .mainCategory(funding.getSubCategory().getMainCategory().getMainCategoryName())
                .subCategory(funding.getSubCategory().getSubCategoryName())
                .thumbnailImgUrl(thumbnailUrl) // 썸네일 URL 추가
                .fundingId(funding.getId())
                .build();
    }
}
