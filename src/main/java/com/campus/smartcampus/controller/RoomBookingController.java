package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.Room;
import com.campus.smartcampus.model.RoomBooking;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.RoomBookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/rooms")
public class RoomBookingController {

    private final RoomBookingService roomBookingService;

    public RoomBookingController(RoomBookingService roomBookingService) {
        this.roomBookingService = roomBookingService;
    }

    @GetMapping
    public String listRooms(@RequestParam(value = "building", required = false) String building,
                            @RequestParam(value = "roomType", required = false) String roomType,
                            @RequestParam(value = "minCapacity", required = false) Integer minCapacity,
                            @RequestParam(value = "facility", required = false) String facility,
                            Model model) {
        model.addAttribute("rooms", roomBookingService.searchRooms(building, roomType, minCapacity, facility));
        model.addAttribute("building", building);
        model.addAttribute("roomType", roomType);
        model.addAttribute("minCapacity", minCapacity);
        model.addAttribute("facility", facility);
        return "rooms/list";
    }

    @GetMapping("/book/{id}")
    public String showBookForm(@PathVariable("id") Long id,
                               @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               HttpSession session,
                               Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Room room = roomBookingService.getRoomById(id);
        model.addAttribute("room", room);

        LocalDate selectedDate = (date != null) ? date : LocalDate.now().plusDays(1);
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("existingBookings", roomBookingService.getBookingsForRoomAndDate(id, selectedDate));

        return "rooms/book";
    }

    @PostMapping("/book")
    public String processBooking(@RequestParam("roomId") Long roomId,
                                 @RequestParam("bookingDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bookingDate,
                                 @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                 @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                                 @RequestParam("purpose") String purpose,
                                 @RequestParam(value = "attendeesCount", defaultValue = "1") Integer attendeesCount,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            RoomBooking booking = roomBookingService.bookRoom(roomId, currentUser, bookingDate, startTime, endTime, purpose, attendeesCount);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Reservation Successful! " + booking.getRoom().getRoomNumber() + " is confirmed for " +
                    booking.getBookingDate() + " (" + booking.getStartTime() + " - " + booking.getEndTime() + ").");
            return "redirect:/rooms/my-bookings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/rooms/book/" + roomId + "?date=" + bookingDate;
        }
    }

    @GetMapping("/my-bookings")
    public String myBookings(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("bookings", roomBookingService.getUserBookings(currentUser));
        return "rooms/my-bookings";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            roomBookingService.cancelBooking(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Room reservation has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/rooms/my-bookings";
    }

    @GetMapping("/manage")
    public String manageRooms(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.FACULTY)) {
            return "redirect:/rooms?error=Unauthorized";
        }
        model.addAttribute("rooms", roomBookingService.getAllRooms());
        model.addAttribute("allBookings", roomBookingService.getAllBookings());
        return "rooms/manage";
    }

    @PostMapping("/add")
    public String addRoom(@ModelAttribute("newRoom") Room room,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/rooms?error=Unauthorized";
        }
        try {
            roomBookingService.saveRoom(room);
            redirectAttributes.addFlashAttribute("successMessage", "New room " + room.getRoomNumber() + " added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding room: " + e.getMessage());
        }
        return "redirect:/rooms/manage";
    }

    @PostMapping("/status/{bookingId}")
    public String updateBookingStatus(@PathVariable("bookingId") Long bookingId,
                                      @RequestParam("status") String status,
                                      @RequestParam(value = "remarks", required = false) String remarks,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.FACULTY)) {
            return "redirect:/rooms?error=Unauthorized";
        }
        try {
            roomBookingService.updateBookingStatus(bookingId, status, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Booking status updated to: " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/rooms/manage";
    }
}
