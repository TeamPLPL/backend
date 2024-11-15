package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.dto.FundingDataDTO;
import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.service.FundingService;
import com.kosa.backend.user.dto.CustomUserDetails;
import com.kosa.backend.user.entity.User;
import com.kosa.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
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
}
