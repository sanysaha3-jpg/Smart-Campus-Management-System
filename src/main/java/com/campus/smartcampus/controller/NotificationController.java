package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.NotificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String viewNotifications(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("notifications", notificationService.getAllUserNotifications(currentUser));
        return "notifications/list";
    }

    @PostMapping("/read/{id}")
    public String markRead(@PathVariable("id") Long id,
                           @RequestParam(value = "redirect", defaultValue = "/notifications") String redirect,
                           HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        notificationService.markAsRead(id);
        return "redirect:" + redirect;
    }

    @PostMapping("/read-all")
    public String markAllRead(HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        notificationService.markAllAsRead(currentUser);
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read.");
        return "redirect:/notifications";
    }
}
