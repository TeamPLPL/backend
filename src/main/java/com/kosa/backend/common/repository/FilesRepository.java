package com.kosa.backend.common.repository;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FilesRepository extends JpaRepository<Files, Integer> {

    Optional<Files> findByFundingIdAndImgType(int fundingId, ImgType imgType);

    Optional<Files> findByUserIdAndImgType(int userId, ImgType imgType);

//    int countAllByFundingIdAndImgType(int fundingId, ImgType imgType);

    List<Files> findAllByFundingIdAndImgType(int fundingId, ImgType imgType);

    Optional<Files> findBySavedNm(String savedNm);

    Optional<Files> findByOriginalNm(String originalNm);

    @Query("SELECT f FROM Files f JOIN FilesSequence fs ON f.id = fs.files.id " +
            "WHERE f.funding.id = :fundingId AND f.imgType = :imgType " +
            "ORDER BY fs.id ASC")
    List<Files> findAllByFundingIdAndImgTypeOrderBySequence(
            @Param("fundingId") int fundingId,
            @Param("imgType") ImgType imgType
    );
}
