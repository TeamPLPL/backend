package com.kosa.backend.common.repository;

import com.kosa.backend.common.entity.Files;
import com.kosa.backend.common.entity.FilesSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilesSequenceRepository extends JpaRepository<FilesSequence, Integer> {
    void save(Files savedFile);
    void deleteByFilesId(int filesId);
    void updateByFilesId(int filesId);
}
