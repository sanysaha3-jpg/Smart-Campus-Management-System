package com.campus.smartcampus.repository;

import com.campus.smartcampus.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByStatus(String status);
    List<Room> findByBuilding(String building);
    List<Room> findByRoomType(String roomType);

    @Query("SELECT r FROM Room r WHERE " +
           "(:building IS NULL OR :building = '' OR r.building = :building) AND " +
           "(:roomType IS NULL OR :roomType = '' OR r.roomType = :roomType) AND " +
           "(:minCapacity IS NULL OR r.capacity >= :minCapacity) AND " +
           "(:facility IS NULL OR :facility = '' OR LOWER(r.facilities) LIKE LOWER(CONCAT('%', :facility, '%'))) AND " +
           "r.status = 'AVAILABLE' " +
           "ORDER BY r.building ASC, r.roomNumber ASC")
    List<Room> searchRooms(
            @Param("building") String building,
            @Param("roomType") String roomType,
            @Param("minCapacity") Integer minCapacity,
            @Param("facility") String facility);
}
