package com.kosa.backend.funding.project.repository;

import com.kosa.backend.funding.project.entity.BusinessMaker;
import com.kosa.backend.funding.project.entity.PersonalMaker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersoanlMakerRepository extends JpaRepository<PersonalMaker, Integer> {
}
