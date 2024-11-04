package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    List<SubCategory> findAllByMainCategory_Id(int id);
}
