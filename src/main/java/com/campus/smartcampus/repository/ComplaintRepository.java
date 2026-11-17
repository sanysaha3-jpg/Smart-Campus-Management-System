package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.Complaint;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findAllByOrderByCreatedAtDesc();
    List<Complaint> findByReportedByOrderByCreatedAtDesc(User reportedBy);
    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);
    List<Complaint> findByCategoryOrderByCreatedAtDesc(String category);

    @Query("SELECT c FROM Complaint c WHERE " +
           "(:status IS NULL OR :status = '' OR c.status = :status) AND " +
           "(:category IS NULL OR :category = '' OR c.category = :category) AND " +
           "(:priority IS NULL OR :priority = '' OR c.priority = :priority) " +
           "ORDER BY " +
           "CASE WHEN c.priority = 'URGENT' THEN 1 WHEN c.priority = 'HIGH' THEN 2 WHEN c.priority = 'MEDIUM' THEN 3 ELSE 4 END ASC, " +
           "c.createdAt DESC")
    List<Complaint> filterComplaints(
            @Param("status") String status,
            @Param("category") String category,
            @Param("priority") String priority);
}
