package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CampusEventService eventService;
    private final RoomBookingService roomBookingService;
    private final LostFoundService lostFoundService;
    private final NoticeService noticeService;
    private final ComplaintService complaintService;
    private final UserService userService;
    private final CampusLocationService locationService;

    public DashboardController(CampusEventService eventService,
                               RoomBookingService roomBookingService,
                               LostFoundService lostFoundService,
                               NoticeService noticeService,
                               ComplaintService complaintService,
                               UserService userService,
                               CampusLocationService locationService) {
        this.eventService = eventService;
        this.roomBookingService = roomBookingService;
        this.lostFoundService = lostFoundService;
        this.noticeService = noticeService;
        this.complaintService = complaintService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("urgentNotices", noticeService.getUrgentNotices());
        model.addAttribute("recentNotices", noticeService.getAllNotices().stream().limit(4).toList());
        model.addAttribute("upcomingEvents", eventService.getAllEvents().stream().limit(3).toList());
        model.addAttribute("featuredRooms", roomBookingService.getAllRooms().stream().limit(4).toList());
        model.addAttribute("recentLostFound", lostFoundService.getAllItems().stream().limit(4).toList());
        model.addAttribute("campusLocations", locationService.getAllLocations().stream().limit(6).toList());

        // Campus stats
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("totalRooms", roomBookingService.getAllRooms().size());
        model.addAttribute("totalLocations", locationService.getAllLocations().size());
        model.addAttribute("totalUsers", userService.countAll());

        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("user", currentUser);
        model.addAttribute("urgentNotices", noticeService.getUrgentNotices());

        if (currentUser.getRole() == Role.ADMIN) {
            // Admin stats
            model.addAttribute("allUsersCount", userService.getAllUsers().size());
            model.addAttribute("allEventsCount", eventService.getAllEvents().size());
            model.addAttribute("allBookingsCount", roomBookingService.getAllBookings().size());
            model.addAttribute("allComplaintsCount", complaintService.getAllComplaints().size());
            model.addAttribute("allLostItemsCount", lostFoundService.getAllItems().size());

            model.addAttribute("recentComplaints", complaintService.getAllComplaints().stream().limit(5).toList());
            model.addAttribute("recentBookings", roomBookingService.getAllBookings().stream().limit(5).toList());
            model.addAttribute("recentEvents", eventService.getAllEvents().stream().limit(5).toList());
        } else if (currentUser.getRole() == Role.ORGANIZER) {
            // Organizer dashboard
            model.addAttribute("myEvents", eventService.getEventsByOrganizer(currentUser));
            model.addAttribute("myBookings", roomBookingService.getUserBookings(currentUser));
        } else {
            // Student & Faculty dashboard
            model.addAttribute("myRegistrations", eventService.getUserRegistrations(currentUser));
            model.addAttribute("myBookings", roomBookingService.getUserBookings(currentUser));
            model.addAttribute("myComplaints", complaintService.getUserComplaints(currentUser));
            model.addAttribute("myItems", lostFoundService.getItemsReportedByUser(currentUser));
            model.addAttribute("myClaims", lostFoundService.getUserClaims(currentUser));
            model.addAttribute("upcomingEvents", eventService.getAllEvents().stream().limit(4).toList());
        }

        return "dashboard";
    }
}
