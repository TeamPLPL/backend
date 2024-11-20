package com.kosa.backend.funding.project.entity;

import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUB_CATEGORY")
public class SubCategory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String subCategoryName;

    @ManyToOne
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;

    @OneToMany(mappedBy = "subCategory")
    private List<Funding> fundings;

    public static SubCategory of(MainCategory mainCategory, String subCategoryName) {
        SubCategory subCategory = new SubCategory();
        subCategory.setMainCategory(mainCategory);
        subCategory.setSubCategoryName(subCategoryName);
        return subCategory;
    }

}
