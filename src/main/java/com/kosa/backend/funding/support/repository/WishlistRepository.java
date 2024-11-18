package com.kosa.backend.funding.support.repository;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.support.entity.Wishlist;
import com.kosa.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    int countByFunding(Funding funding);
    boolean existsByUserIdAndFundingId(Integer userId, Integer fundingId);

    @Query("SELECT w.funding.id FROM Wishlist w WHERE w.user.id = :userId")
    List<Integer> findFundingIdsByUserId(@Param("userId") int userId);

    void deleteByUserIdAndFundingId(int userId, int fundingId);
}
