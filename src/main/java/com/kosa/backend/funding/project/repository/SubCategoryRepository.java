package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {
    List<SubCategory> findAllByMainCategory_Id(int id);
}
