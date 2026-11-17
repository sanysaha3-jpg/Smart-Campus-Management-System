package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.Room;
import com.campus.smartcampus.model.RoomBooking;
import com.campus.smartcampus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {
    List<RoomBooking> findByUserOrderByBookingDateDescStartTimeDesc(User user);
    List<RoomBooking> findByRoomAndBookingDateOrderByStartTimeAsc(Room room, LocalDate bookingDate);
    List<RoomBooking> findByBookingDateOrderByStartTimeAsc(LocalDate bookingDate);
    List<RoomBooking> findAllByOrderByBookingDateDescStartTimeDesc();

    // Prevent overlapping reservations query
    @Query("SELECT b FROM RoomBooking b WHERE b.room.id = :roomId " +
           "AND b.bookingDate = :bookingDate " +
           "AND b.status IN ('CONFIRMED', 'PENDING') " +
           "AND (:startTime < b.endTime AND :endTime > b.startTime)")
    List<RoomBooking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("bookingDate") LocalDate bookingDate,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
}
