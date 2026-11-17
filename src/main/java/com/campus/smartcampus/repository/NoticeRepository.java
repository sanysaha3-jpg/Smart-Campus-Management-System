package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByPublishedDateDesc();
    List<Notice> findByUrgentTrueOrderByPublishedDateDesc();
    List<Notice> findByDepartmentOrderByPublishedDateDesc(String department);
    List<Notice> findByCategoryOrderByPublishedDateDesc(String category);

    @Query("SELECT n FROM Notice n WHERE " +
           "(:department IS NULL OR :department = '' OR n.department = :department OR n.department = 'All Departments') AND " +
           "(:category IS NULL OR :category = '' OR n.category = :category) AND " +
           "(:audience IS NULL OR :audience = '' OR n.targetAudience = 'ALL' OR n.targetAudience = :audience) " +
           "ORDER BY n.urgent DESC, n.publishedDate DESC")
    List<Notice> filterNotices(
            @Param("department") String department,
            @Param("category") String category,
            @Param("audience") String audience);
}
