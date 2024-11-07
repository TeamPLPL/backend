package com.kosa.backend.funding.project.controller;

import com.kosa.backend.funding.project.dto.FundingDTO;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.service.FundingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/funding")
public class FundingController {

    private final FundingService fundingService;

    @GetMapping("/main-categories")
    public ResponseEntity<List<MainCategory>> getMainCategory() {
        return ResponseEntity.ok(fundingService.getMainCategories());
    }

    @GetMapping("/sub-categories/{parent-id}")
    public ResponseEntity<List<SubCategory>> getSubCategory(@PathVariable(name="parent-id") Integer parentId) {
        return fundingService.getSubCategoriesById(parentId);
    }

    @PostMapping("/fundinglist/new")
    public ResponseEntity<List<FundingDTO>> getNewFundingList() {
        return fundingService.getNewFundingList();
    }

    @PostMapping("/fundinglist/top")
    public ResponseEntity<List<FundingDTO>> getTopFundingList() { return fundingService.getTopFundingList(); }
}
