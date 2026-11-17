package com.campus.smartcampus.service;

import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.Room;
import com.campus.smartcampus.model.RoomBooking;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.RoomBookingRepository;
import com.campus.smartcampus.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class RoomBookingService {

    private final RoomRepository roomRepository;
    private final RoomBookingRepository bookingRepository;
    private final NotificationService notificationService;

    public RoomBookingService(RoomRepository roomRepository,
                              RoomBookingRepository bookingRepository,
                              NotificationService notificationService) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> searchRooms(String building, String roomType, Integer minCapacity, String facility) {
        return roomRepository.searchRooms(building, roomType, minCapacity, facility);
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + id));
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public boolean isRoomAvailable(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
            return false;
        }
        List<RoomBooking> overlapping = bookingRepository.findOverlappingBookings(roomId, date, startTime, endTime);
        return overlapping.isEmpty();
    }

    public List<RoomBooking> getOverlappingBookings(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return bookingRepository.findOverlappingBookings(roomId, date, startTime, endTime);
    }

    public RoomBooking bookRoom(Long roomId, User user, LocalDate date, LocalTime startTime, LocalTime endTime, String purpose, Integer attendees) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }

        Room room = getRoomById(roomId);
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            throw new IllegalStateException("Room " + room.getRoomNumber() + " is currently under maintenance.");
        }

        if (attendees != null && attendees > room.getCapacity()) {
            throw new IllegalArgumentException("Attendee count (" + attendees + ") exceeds room capacity (" + room.getCapacity() + ").");
        }

        List<RoomBooking> conflicts = bookingRepository.findOverlappingBookings(roomId, date, startTime, endTime);
        if (!conflicts.isEmpty()) {
            RoomBooking conflict = conflicts.get(0);
            throw new IllegalStateException("Reservation Conflict! Room " + room.getRoomNumber() +
                    " is already reserved on " + date + " between " + conflict.getStartTime() + " and " + conflict.getEndTime() +
                    " for: '" + conflict.getPurpose() + "'. Please choose another slot or room.");
        }

        RoomBooking booking = new RoomBooking();
        booking.setRoom(room);
        booking.setUser(user);
        booking.setBookingDate(date);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPurpose(purpose);
        booking.setAttendeesCount(attendees != null ? attendees : 1);
        booking.setStatus("CONFIRMED");

        RoomBooking saved = bookingRepository.save(booking);

        // Notify user
        notificationService.notifyUser(
                user,
                "Room Booking Confirmed: " + room.getRoomNumber(),
                "Your booking for " + room.getRoomNumber() + " (" + room.getBuilding() + ") on " + date + " from " + startTime + " to " + endTime + " is confirmed.",
                "/rooms/my-bookings",
                "BOOKING"
        );

        return saved;
    }

    public List<RoomBooking> getUserBookings(User user) {
        return bookingRepository.findByUserOrderByBookingDateDescStartTimeDesc(user);
    }

    public List<RoomBooking> getAllBookings() {
        return bookingRepository.findAllByOrderByBookingDateDescStartTimeDesc();
    }

    public List<RoomBooking> getBookingsForRoomAndDate(Long roomId, LocalDate date) {
        Room room = getRoomById(roomId);
        return bookingRepository.findByRoomAndBookingDateOrderByStartTimeAsc(room, date);
    }

    public void cancelBooking(Long bookingId, User user) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        if (!booking.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Not authorized to cancel this reservation.");
        }
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        notificationService.notifyUser(
                booking.getUser(),
                "Booking Cancelled: " + booking.getRoom().getRoomNumber(),
                "Your booking for " + booking.getRoom().getRoomNumber() + " on " + booking.getBookingDate() + " has been cancelled.",
                "/rooms/my-bookings",
                "BOOKING"
        );
    }

    public void updateBookingStatus(Long bookingId, String status, String remarks) {
        RoomBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + bookingId));
        booking.setStatus(status);
        booking.setAdminRemarks(remarks);
        bookingRepository.save(booking);

        notificationService.notifyUser(
                booking.getUser(),
                "Booking Status Updated: " + status,
                "Your booking for " + booking.getRoom().getRoomNumber() + " is now " + status + ". Remarks: " + remarks,
                "/rooms/my-bookings",
                "BOOKING"
        );
    }
}
