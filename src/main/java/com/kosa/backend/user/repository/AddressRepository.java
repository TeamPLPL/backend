package com.kosa.backend.user.repository;

import com.kosa.backend.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findAllByUserId(int userId);
    Optional<Address> findByUserIdAndIsDefaultTrue(int userId);
}