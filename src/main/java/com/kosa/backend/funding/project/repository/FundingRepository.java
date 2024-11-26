package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.dto.FundingWithSupporterCntDTO;
import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.user.entity.Maker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Integer> {

    List<Funding> findAllByOrderByPublishDateDesc(Pageable pageable);

//    @Query("SELECT f, COUNT(fs.id) AS supportCount " +
//            "FROM Funding f LEFT JOIN f.fundingSupport fs " +
//            "WHERE f.fundingStartDate <= :currentDate AND f.fundingEndDate >= :currentDate " +
//            "GROUP BY f.id, f.fundingTitle, f.fundingStartDate, f.fundingEndDate " +
//            "ORDER BY supportCount DESC")
//    List<FundingWithSupporterCntDTO> findFundingWithSupportCountAndDateCondition(Pageable pageable, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT f, COUNT(DISTINCT fs.user.id) as supportCount " +
            "FROM Funding f " +
            "LEFT JOIN FundingSupport fs ON f.id = fs.funding.id " +
            "WHERE f.fundingStartDate <= :currentDate AND f.fundingEndDate >= :currentDate AND f.isPublished = true " +
            "GROUP BY f.id " +
            "ORDER BY supportCount DESC")
    Page<Object[]> findAllPublishedWithSupporterCount(Pageable pageable, @Param("currentDate") LocalDateTime currentDate);

    @Query("SELECT f.id FROM Funding f WHERE f.fundingStartDate <= :currentDate AND f.fundingEndDate >= :currentDate")
    List<Integer> findAllCurrentFundingIds(@Param("currentDate") LocalDateTime currentDate);


    // 작성자 : 신은호, 작성 내용 : maker에 의한 프로젝트 조회
    List<Funding> findAllByMaker(Maker maker);

    List<Funding> findAllBySubCategory_IdIn(List<Integer> subCategoryIds);
    List<Funding> findAllBySubCategory_Id(Integer subCategoryId);

//    Page<Funding> findByFundingTitleContainingAndIsPublishedTrue(String title, Pageable pageable);

//    @Query("SELECT f, COUNT(DISTINCT fs.user.id) as supportCount " +
//            "FROM Funding f " +
//            "LEFT JOIN FundingSupport fs ON f.id = fs.funding.id " +
//            "WHERE f.fundingTitle LIKE %:title% AND f.isPublished = true " +
//            "GROUP BY f.id " +
//            "ORDER BY supportCount DESC")
//    Page<Object[]> findByFundingTitleContainingAndIsPublishedTrueOrderBySupportCount(
//            @Param("title") String title,
//            Pageable pageable
//    );

        @Query("SELECT f, COUNT(DISTINCT fs.user.id) as supportCount " +
            "FROM Funding f " +
            "LEFT JOIN FundingSupport fs ON f.id = fs.funding.id " +
            "WHERE f.fundingTitle LIKE %:title% AND f.isPublished = true " +
            "GROUP BY f.id " +
            "ORDER BY supportCount DESC")
    List<Funding> findByFundingTitleContainingAndIsPublishedTrueOrderBySupportCount(@Param("title") String title);

    List<Funding> findByFundingTitleContainingAndIsPublishedTrue(String title);
}
