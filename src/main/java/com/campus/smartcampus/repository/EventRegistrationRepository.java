package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.CampusEvent;
import com.campus.smartcampus.model.EventRegistration;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByUserOrderByRegistrationDateDesc(User user);
    List<EventRegistration> findByEvent(CampusEvent event);
    Optional<EventRegistration> findByEventAndUser(CampusEvent event, User user);
    boolean existsByEventAndUserAndStatus(CampusEvent event, User user, String status);
    long countByEventAndStatus(CampusEvent event, String status);
}
