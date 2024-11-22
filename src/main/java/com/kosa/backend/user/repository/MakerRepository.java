package com.kosa.backend.user.repository;

import com.kosa.backend.user.entity.Maker;
import com.kosa.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MakerRepository extends JpaRepository<Maker, Integer> {
    Optional<Maker> findByUser(User user);
}
