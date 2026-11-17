package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.ItemClaim;
import com.campus.smartcampus.model.LostFoundItem;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemClaimRepository extends JpaRepository<ItemClaim, Long> {
    List<ItemClaim> findByItemOrderByClaimDateDesc(LostFoundItem item);
    List<ItemClaim> findByClaimantOrderByClaimDateDesc(User claimant);
    List<ItemClaim> findByStatusOrderByClaimDateDesc(String status);
    boolean existsByItemAndClaimant(LostFoundItem item, User claimant);
}
