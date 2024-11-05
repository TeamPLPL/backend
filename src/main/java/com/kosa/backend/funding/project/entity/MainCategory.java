package com.kosa.backend.funding.project.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.kosa.backend.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "MAIN_CATEGORY")
public class MainCategory extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String mainCategoryName;

    @OneToMany(mappedBy = "mainCategory", cascade = CascadeType.ALL)
    private List<SubCategory> subCategory;

    // 정적 팩토리 메서드
    public static MainCategory of(String mainCategoryName) {
        MainCategory mainCategory = new MainCategory();
        mainCategory.setMainCategoryName(mainCategoryName);
        return mainCategory;
    }
}
