package com.kosa.backend.funding.project.controller;

import com.kosa.backend.common.entity.Const;
import com.kosa.backend.funding.project.dto.*;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.service.FundingService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import com.kosa.backend.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/funding")
public class FundingController {

    private final FundingService fundingService;
    private final UserService userService;

    @GetMapping("/main-categories")
    public ResponseEntity<List<MainCategoryDTO>> getMainCategory() {
        List<MainCategoryDTO> mainCategoryDTOList = fundingService.getMainCategories();
        if(mainCategoryDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(fundingService.getMainCategories());
    }

    @GetMapping("/sub-categories/{parent-id}")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategory(@PathVariable(name="parent-id") Integer parentId) {
        List<SubCategoryDTO> subCategoryDTOList = fundingService.getSubCategoriesById(parentId);
        if(subCategoryDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity.ok(subCategoryDTOList);
    }

    @GetMapping("/fundinglist/new")
    public ResponseEntity<List<FundingDTO>> getNewFundingList() {
        return fundingService.getNewFundingList();
    }

    @GetMapping("/fundinglist/top")
    public ResponseEntity<List<FundingDTO>> getTopFundingList() {
        return fundingService.getTopFundingList();
    }

    @GetMapping("/fundinglist/main/{id}")
    public ResponseEntity<Page<FundingDTO>> getFundingListByMainId(
            @PathVariable(name = "id") Integer mainCategoryId,
            @PageableDefault(size = Const.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<FundingDTO> fundingDTOPage = fundingService.getFundingDTOPageByMainCategoryId(mainCategoryId, pageable);
        if (fundingDTOPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fundingDTOPage);
    }

    @GetMapping("/fundinglist/sub/{id}")
    public ResponseEntity<Page<FundingDTO>> getFundingListBySubId(
            @PathVariable(name = "id") Integer subCategoryId,
            @PageableDefault(size = Const.DEFAULT_PAGE_SIZE) Pageable pageable) {
        Page<FundingDTO> fundingDTOPage = fundingService.getFundingDTOPageBySubCategoryId(subCategoryId, pageable);
        if (fundingDTOPage.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fundingDTOPage);
    }

    @PostMapping("/funding-data/{id}")
    public ResponseEntity<FundingDataDTO> getFundingData(@AuthenticationPrincipal CustomUserDetails cud, @PathVariable(name="id") Integer fundingId) {
        int userId = 0;
        if(cud != null) {
            String userEmail = cud.getUsername();
            User user = userService.getUser(userEmail);
            if(user != null) {userId = user.getId(); }
        }

        FundingDataDTO fdDTO = fundingService.getFundingData(userId, fundingId);
        if(fdDTO == null) { return new ResponseEntity<>(HttpStatus.NO_CONTENT); }

        return ResponseEntity.ok(fdDTO);
    }

    @PostMapping("/funding-imgs/{id}")
    public ResponseEntity<FundingImgListDTO> getFundingImgList(@PathVariable(name="id") Integer fundingId) {
        return ResponseEntity.ok(fundingService.getFundingImgList(fundingId));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<FundingDTO>> searchTitle(
            @RequestParam("title") String title,
            @PageableDefault(size = Const.DEFAULT_PAGE_SIZE) Pageable pageable) {

        Page<FundingDTO> searchResults = fundingService.searchByTitle(title, pageable);
        return ResponseEntity.ok(new PagedModel<>(searchResults));
    }

    @GetMapping("/fundings")
    public ResponseEntity<Page<FundingDTO>> getFundings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FundingDTO> fundingDTOs = fundingService.getFundingsOrderBySupporterCount(pageable);

        return ResponseEntity.ok(fundingDTOs);
    }

//    @GetMapping("/{id}/notice")
//    public ResponseEntity<Page<FundingNoticeDTO>> getFundingNoticeDTOList(
//        @PathVariable(name="id") Integer fundingId,
//        @PageableDefault(size = Const.DEFAULT_PAGE_SIZE) Pageable pageable
//    ) {
//        Page<FundingNoticeDTO> fundingNoticeDTO = fundingService.getFundingNoticeDTOList(fundingId, pageable);
//        if (fundingNoticeDTO.isEmpty()) {
//            return ResponseEntity.noContent().build();
//        }
//        return ResponseEntity.ok(fundingNoticeDTO);F
//    }

    @GetMapping("/{id}/notice")
    public ResponseEntity<Page<FundingNoticeDTO>> getFundingNoticeDTOList(
            @PathVariable(name="id") Integer fundingId,
            @PageableDefault(size = 5, page = 0) Pageable pageable
    ) {
        Funding funding = fundingService.getFundingById(fundingId);
        if(funding == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Page<FundingNoticeDTO> fundingNoticeDTO = fundingService.getFundingNoticeDTOList(fundingId, pageable);
        if (fundingNoticeDTO.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fundingNoticeDTO);
    }

    @PostMapping("/{id}/notice/create")
    public ResponseEntity<Void> addNewFundingNotice(@AuthenticationPrincipal CustomUserDetails cud,
                                                    @PathVariable(name="id") Integer fundingId,
                                                    @RequestBody FundingNoticeDTO fnDTO) {

        Funding funding = fundingService.getFundingById(fundingId);
        if(funding == null || fnDTO.getNoticeTitle() == null || fnDTO.getNoticeContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = CommonUtils.getCurrentUser(cud, userService);
        Maker maker = funding.getMaker();
        if(user == null || user.getId() != maker.getId() ) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        try {
            fundingService.saveNewFundingNotice(funding, fnDTO);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/notice/{noticeId}")
    public ResponseEntity<FundingNoticeDTO> getFundingNoticeDTO(
            @PathVariable(name="id") Integer fundingId,
            @PathVariable(name="noticeId") Integer noticeId
    ) {
        FundingNoticeDTO fundingNoticeDTO = fundingService.getFundingnoticeDTO(noticeId);
        if (fundingNoticeDTO == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(fundingNoticeDTO);
    }

    @PutMapping("/{id}/notice/{noticeId}")
    public ResponseEntity<?> updateFundingNotice(
            @AuthenticationPrincipal CustomUserDetails cud,
            @PathVariable(name="id") Integer fundingId,
            @RequestBody FundingNoticeDTO fnDTO
    ) {
        Funding funding = fundingService.getFundingById(fundingId);
        if(funding == null || fnDTO.getNoticeTitle() == null || fnDTO.getNoticeContent() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = CommonUtils.getCurrentUser(cud, userService);
        Maker maker = funding.getMaker();
        if(user == null || user.getId() != maker.getId() ) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        if(fnDTO.getNoticeId() < 1) {return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); }
        try {
            fundingService.updateFundingNotice(fnDTO);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/notice/{noticeId}")
    public ResponseEntity<Integer> deleteFundingNotice(
            @AuthenticationPrincipal CustomUserDetails cud,
            @PathVariable(name="id") Integer fundingId,
            @PathVariable(name="noticeId") Integer noticeId
    ) {
        Funding funding = fundingService.getFundingById(fundingId);
        if(funding == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        User user = CommonUtils.getCurrentUser(cud, userService);
        Maker maker = funding.getMaker();
        if(user == null || user.getId() != maker.getId() ) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }

        try {
            fundingService.deleteFundingNotice(noticeId);
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }
}


