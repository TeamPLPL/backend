package com.kosa.backend.common.repository;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.enums.ImgType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository


public interface FilesRepository extends JpaRepository<Files, Integer> {

    Optional<Files> findByIdAndImgType(int id, ImgType imgType);
}
