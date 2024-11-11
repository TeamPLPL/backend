package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funding")
public class FundingController {

    private final FundingService fundingService;

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
    public ResponseEntity<List<FundingDTO>> getTopFundingList() { return fundingService.getTopFundingList(); }
}
