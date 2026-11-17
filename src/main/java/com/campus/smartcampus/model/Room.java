package com.campus.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @NotBlank(message = "Building name is required")
    @Column(nullable = false, length = 100)
    private String building;

    @NotBlank(message = "Room type is required")
    @Column(name = "room_type", nullable = false, length = 50)
    private String roomType; // CLASSROOM, LAB, SEMINAR_HALL, STUDY_ROOM, AUDITORIUM

    @NotNull(message = "Capacity is required")
    @Column(nullable = false)
    private Integer capacity;

    @Column(length = 255)
    private String facilities; // e.g., "Projector, AC, High-Speed Wi-Fi, Smart Board, Audio System"

    @Column(length = 20)
    private String floor;

    @Column(length = 30)
    private String status = "AVAILABLE"; // AVAILABLE, UNDER_MAINTENANCE

    public Room() {}

    public Room(String roomNumber, String building, String roomType, Integer capacity, String facilities, String floor) {
        this.roomNumber = roomNumber;
        this.building = building;
        this.roomType = roomType;
        this.capacity = capacity;
        this.facilities = facilities;
        this.floor = floor;
        this.status = "AVAILABLE";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public String getFacilities() { return facilities; }
    public void setFacilities(String facilities) { this.facilities = facilities; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
