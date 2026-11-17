package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.CampusEvent;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
    List<CampusEvent> findAllByOrderByEventDateAsc();
    List<CampusEvent> findByStatusOrderByEventDateAsc(String status);
    List<CampusEvent> findByOrganizerOrderByEventDateAsc(User organizer);

    @Query("SELECT e FROM CampusEvent e WHERE " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:category IS NULL OR :category = '' OR e.category = :category) AND " +
           "(:department IS NULL OR :department = '' OR e.department = :department) AND " +
           "(:date IS NULL OR e.eventDate = :date) " +
           "ORDER BY e.eventDate ASC")
    List<CampusEvent> searchEvents(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("department") String department,
            @Param("date") LocalDate date);
}
