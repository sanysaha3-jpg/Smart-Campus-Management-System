package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.LostFoundItem;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LostFoundItemRepository extends JpaRepository<LostFoundItem, Long> {
    List<LostFoundItem> findAllByOrderByItemDateDesc();
    List<LostFoundItem> findByTypeOrderByItemDateDesc(String type);
    List<LostFoundItem> findByReportedByOrderByItemDateDesc(User user);
    List<LostFoundItem> findByStatus(String status);

    @Query("SELECT item FROM LostFoundItem item WHERE " +
           "(:type IS NULL OR :type = '' OR item.type = :type) AND " +
           "(:category IS NULL OR :category = '' OR item.category = :category) AND " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(item.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(item.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(item.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR :status = '' OR item.status = :status) " +
           "ORDER BY item.itemDate DESC")
    List<LostFoundItem> searchItems(
            @Param("type") String type,
            @Param("category") String category,
            @Param("keyword") String keyword,
            @Param("status") String status);
}
