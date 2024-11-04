package com.kosa.backend.funding.project.service;

import com.kosa.backend.api.S3Service;
import com.kosa.backend.common.entity.Const;
import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.project.repository.MainCategoryRepository;
import com.kosa.backend.funding.project.repository.SubCategoryRepository;
import com.kosa.backend.funding.support.entity.FundingSupport;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import com.kosa.backend.funding.support.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    public List<MainCategory> getMainCategories() {
        return mainCategoryRepository.findAll();
    }

    public ResponseEntity<List<SubCategory>> getSubCategoriesById(int parentId) {
        List<SubCategory> subCategoryList = subCategoryRepository.findAllByMainCategory_Id(parentId);
        if (subCategoryList == null || subCategoryList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(subCategoryList);
    }

    public ResponseEntity<List<FundingDTO>> getNewFundingList() {
        PageRequest pageRequest = PageRequest.of(0, Const.NEW_FUNDINGLIST_CNT, Sort.by(Sort.Direction.DESC, "publishDate"));
        List<Funding> newFundingList = fundingRepository.findTopByOrderByPublishDateDesc(pageRequest);
        if(newFundingList == null || newFundingList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<FundingDTO> fundingDTOList = new ArrayList<>();
        for(Funding funding : newFundingList) {
            InputStreamResource isr = null;
            try {
                isr = s3Service.downloadThumbnailByFundingId(funding.getId());
            } catch(Exception e) {
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }

            FundingDTO nfDto = FundingDTO.builder()
                    .id(funding.getId())
                    .fundingTitle(funding.getFundingTitle())
                    .makerNick(funding.getMaker().getUser().getUserNick())
                    .supportCnt(getFundingSupportUserCounts(funding))
                    .wishlistCnt(wishlistRepository.countByFunding(funding))
                    .thumbnailImg(isr)
                    .build();

            fundingDTOList.add(nfDto);
        }

        return ResponseEntity.ok(fundingDTOList);
    }

    public int getFundingSupportUserCounts(Funding funding) {
        if(funding == null) { return Const.NULL; }

        List<FundingSupport> supports = fundingSupportRepository.findByFundingId(funding.getId());

        int supporterCnt = (int) supports.stream()
                .map(support -> support.getUser().getId())
                .distinct()
                .count();

        return supporterCnt;
    }

//    public ResponseEntity<List<Funding>> getTopfundingList(int cnt) {
//        List<Funding> topfundingList = fundingRepository.findTopNByOrderByCountByFundingSupportDesc(cnt);
//        if(topfundingList == null || topfundingList.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//        }
//        return ResponseEntity.ok(topfundingList);
//    }
}
