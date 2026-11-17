package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final CampusEventService eventService;
    private final RoomBookingService roomBookingService;
    private final ComplaintService complaintService;
    private final LostFoundService lostFoundService;

    public AdminController(UserService userService,
                           CampusEventService eventService,
                           RoomBookingService roomBookingService,
                           ComplaintService complaintService,
                           LostFoundService lostFoundService) {
        this.userService = userService;
        this.eventService = eventService;
        this.roomBookingService = roomBookingService;
        this.complaintService = complaintService;
        this.lostFoundService = lostFoundService;
    }

    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=Unauthorized";
        }
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @PostMapping("/users/toggle/{id}")
    public String toggleUser(@PathVariable("id") Long id,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=Unauthorized";
        }
        if (currentUser.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You cannot disable your own admin account.");
            return "redirect:/admin/users";
        }
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "User status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/role/{id}")
    public String changeRole(@PathVariable("id") Long id,
                             @RequestParam("role") Role role,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=Unauthorized";
        }
        try {
            User user = userService.findById(id).orElseThrow();
            user.setRole(role);
            userService.updateProfile(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Role updated for " + user.getUsername() + " to " + role);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/overview")
    public String systemOverview(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/dashboard?error=Unauthorized";
        }

        model.addAttribute("totalUsers", userService.countAll());
        model.addAttribute("totalEvents", eventService.getAllEvents().size());
        model.addAttribute("totalRooms", roomBookingService.getAllRooms().size());
        model.addAttribute("totalBookings", roomBookingService.getAllBookings().size());
        model.addAttribute("totalComplaints", complaintService.getAllComplaints().size());
        model.addAttribute("totalLostItems", lostFoundService.getAllItems().size());

        model.addAttribute("students", userService.getUsersByRole(Role.STUDENT).size());
        model.addAttribute("faculty", userService.getUsersByRole(Role.FACULTY).size());
        model.addAttribute("organizers", userService.getUsersByRole(Role.ORGANIZER).size());
        model.addAttribute("admins", userService.getUsersByRole(Role.ADMIN).size());

        model.addAttribute("allBookings", roomBookingService.getAllBookings());
        model.addAttribute("allComplaints", complaintService.getAllComplaints());
        return "admin/overview";
    }
}
