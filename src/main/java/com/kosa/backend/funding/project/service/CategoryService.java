package com.kosa.backend.funding.project.service;

import com.kosa.backend.funding.project.dto.MainCategoryDTO;
import com.kosa.backend.funding.project.dto.SubCategoryDTO;
import com.kosa.backend.funding.project.entity.MainCategory;
import com.kosa.backend.funding.project.entity.SubCategory;
import com.kosa.backend.funding.project.repository.MainCategoryRepository;
import com.kosa.backend.funding.project.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final MainCategoryRepository mainCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public List<MainCategoryDTO> findMainCategoryAll() {
        List<MainCategory> mainCategories = mainCategoryRepository.findAll();
        List<MainCategoryDTO> mainCategoriesDTO = new ArrayList<MainCategoryDTO>();
        for (MainCategory mainCategory : mainCategories) {
            mainCategoriesDTO.add(MainCategoryDTO.fromMainCategory(mainCategory));
        }

        return mainCategoriesDTO;
    }

    public List<SubCategoryDTO> findSubCategoryAll() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        List<SubCategoryDTO> subCategoriesDTO = new ArrayList<>();
        for (SubCategory subCategory : subCategories) {
            subCategoriesDTO.add(SubCategoryDTO.fromSubCategories(subCategory));
        }
        return subCategoriesDTO;
    }
}
