package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.CampusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusLocationRepository extends JpaRepository<CampusLocation, Long> {
    List<CampusLocation> findAllByOrderByNameAsc();
    List<CampusLocation> findByCategoryOrderByNameAsc(String category);
    List<CampusLocation> findByBlockOrderByNameAsc(String block);

    @Query("SELECT loc FROM CampusLocation loc WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR LOWER(loc.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(loc.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(loc.block) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR loc.category = :category) AND " +
           "(:block IS NULL OR :block = '' OR loc.block = :block) " +
           "ORDER BY loc.block ASC, loc.name ASC")
    List<CampusLocation> searchLocations(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("block") String block);
}
