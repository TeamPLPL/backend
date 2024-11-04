package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.support.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    int countByFunding(Funding funding);
}
