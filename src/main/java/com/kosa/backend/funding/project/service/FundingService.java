package com.kosa.backend.funding.project.service;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.common.service.S3Service;
import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.MainCategoryRepository;
import com.kosa.backend.funding.project.repository.SubCategoryRepository;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import com.kosa.backend.funding.support.repository.WishlistRepository;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class FundingService {
    private final S3Service s3Service;

    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final FundingRepository fundingRepository;
    private final FundingSupportRepository fundingSupportRepository;
    private final WishlistRepository wishlistRepository;

    // 메인 카테고리 리스트 조회
    public List<MainCategoryDTO> getMainCategories() {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        List<MainCategoryDTO> mainCategoryDTOList = new ArrayList<>();
        for(MainCategory mainCategory : mainCategories) {
            MainCategoryDTO mcDTO = MainCategoryDTO.builder()
                    .mainCategoryId(mainCategory.getId())
                    .mainCategoryName(mainCategory.getMainCategoryName())
                    .build();
            mainCategoryDTOList.add(mcDTO);
        }
        return mainCategoryDTOList;
    }

    // 메인 카테고리 id별 서브 카테고리 리스트 조회
    public List<SubCategoryDTO> getSubCategoriesById(int parentId) {
        List<SubCategory> subCategoryList = subCategoryRepository.findAllByMainCategory_Id(parentId);
        List<SubCategoryDTO> subCategoryDTOList = new ArrayList<>();
        for(SubCategory subCategory : subCategoryList) {
         SubCategoryDTO scDTO = SubCategoryDTO.builder()
                 .subCategoryId(subCategory.getId())
                 .subCategoryName(subCategory.getSubCategoryName())
                 .build();
         subCategoryDTOList.add(scDTO);
        }
        return subCategoryDTOList;
    }

    // 최신순 펀딩 리스트 조회 (8개)
    public ResponseEntity<List<FundingDTO>> getNewFundingList() {
        PageRequest pageRequest = PageRequest.of(0, Const.NEW_FUNDINGLIST_CNT, Sort.by(Sort.Direction.DESC, "publishDate"));
        List<Funding> newFundingList = fundingRepository.findTopByOrderByPublishDateDesc(pageRequest);
        return convertToFundingDTOList(newFundingList);
    }

    public ResponseEntity<List<FundingDTO>> getTopFundingList() {

        LocalDateTime currentDate = LocalDateTime.now();
        PageRequest pageRequest = PageRequest.of(0, Const.TOP_FUNDINGLIST_CNT);

        List<Funding> topFundingList = fundingRepository.findTopFundingsWithSupporterCount(pageRequest, currentDate);

        return convertToFundingDTOList(topFundingList);
    }

    // Funding -> FundingDTO 변환 메소드
    public FundingDTO convertToFundingDTO(Funding funding) {
        String thumbnailImgUrl = null;
        try {
            thumbnailImgUrl = s3Service.getThumbnailByFundingId(funding.getId());
        } catch(Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        double achieveRate = CommonUtils.calculateAchievementRate(funding.getCurrentAmount(), funding.getTargetAmount());

        return FundingDTO.builder()
                .id(funding.getId())
                .fundingTitle(funding.getFundingTitle())
                .makerNick(funding.getMaker().getUser().getUserNick())
                .supportCnt(getFundingSupportUserCounts(funding))
                .achievementRate(achieveRate)
                .wishlistCnt(wishlistRepository.countByFunding(funding))
                .thumbnailImgUrl(thumbnailImgUrl)
                .build();
    }

    // FundingList -> FundingDTOList 변환 메소드
    public ResponseEntity<List<FundingDTO>> convertToFundingDTOList(List<Funding> fundingList) {
        if(fundingList == null || fundingList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        List<FundingDTO> fundingDTOList = new ArrayList<>();
        for(Funding funding : fundingList) {
            fundingDTOList.add(convertToFundingDTO(funding));
        }
        return ResponseEntity.ok(fundingDTOList);
    }

    // 펀딩 참여자 수 계산 메소드
    public int getFundingSupportUserCounts(Funding funding) {
        if(funding == null) { return Const.NULL; }

        List<FundingSupport> supports = fundingSupportRepository.findByFundingId(funding.getId());

        int supporterCnt = (int) supports.stream()
                .map(support -> support.getUser().getId())
                .distinct()
                .count();

        return supporterCnt;
    }
}
