package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.repository.MainCategoryRepository;
import com.kosa.backend.funding.project.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FundingService {
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

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
}
