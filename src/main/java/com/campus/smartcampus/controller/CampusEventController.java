package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.CampusEvent;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.CampusEventService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/events")
public class CampusEventController {

    private final CampusEventService eventService;

    public CampusEventController(CampusEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public String listEvents(@RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "category", required = false) String category,
                             @RequestParam(value = "department", required = false) String department,
                             @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             Model model) {
        model.addAttribute("events", eventService.searchEvents(keyword, category, department, date));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("department", department);
        model.addAttribute("date", date);
        return "events/list";
    }

    @GetMapping("/view/{id}")
    public String viewEvent(@PathVariable("id") Long id, HttpSession session, Model model) {
        CampusEvent event = eventService.getEventById(id);
        model.addAttribute("event", event);

        User currentUser = (User) session.getAttribute("currentUser");
        boolean isRegistered = false;
        if (currentUser != null) {
            isRegistered = eventService.isUserRegistered(event, currentUser);
        }
        model.addAttribute("isRegistered", isRegistered);

        if (currentUser != null && (currentUser.getRole() == Role.ADMIN ||
                (event.getOrganizer() != null && event.getOrganizer().getId().equals(currentUser.getId())))) {
            model.addAttribute("attendees", eventService.getEventAttendees(id));
            model.addAttribute("canManage", true);
        } else {
            model.addAttribute("canManage", false);
        }

        return "events/view";
    }

    @PostMapping("/register/{id}")
    public String registerForEvent(@PathVariable("id") Long id,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        try {
            eventService.registerUserForEvent(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for the event!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events/view/" + id;
    }

    @PostMapping("/cancel-registration/{regId}")
    public String cancelRegistration(@PathVariable("regId") Long regId,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            eventService.cancelRegistration(regId, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Event registration cancelled successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events/my-registrations";
    }

    @GetMapping("/my-registrations")
    public String myRegistrations(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        model.addAttribute("registrations", eventService.getUserRegistrations(currentUser));
        return "events/my-registrations";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() == Role.STUDENT) {
            return "redirect:/events?error=Only organizers, faculty, or admins can create events.";
        }
        CampusEvent event = new CampusEvent();
        event.setEventDate(LocalDate.now().plusDays(3));
        model.addAttribute("event", event);
        return "events/create";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute("event") CampusEvent event,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() == Role.STUDENT) {
            return "redirect:/events";
        }
        try {
            CampusEvent saved = eventService.createEvent(event, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Event '" + saved.getTitle() + "' published successfully!");
            return "redirect:/events/view/" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + e.getMessage());
            return "redirect:/events/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        CampusEvent event = eventService.getEventById(id);
        if (!event.getOrganizer().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            return "redirect:/events?error=Not authorized to edit this event.";
        }
        model.addAttribute("event", event);
        return "events/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable("id") Long id,
                              @ModelAttribute("event") CampusEvent event,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            eventService.updateEvent(id, event, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/events/view/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating event: " + e.getMessage());
            return "redirect:/events/edit/" + id;
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelEvent(@PathVariable("id") Long id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            eventService.cancelEvent(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Event has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events/view/" + id;
    }

    @GetMapping("/manage")
    public String manageEvents(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        if (currentUser.getRole() == Role.ADMIN) {
            model.addAttribute("events", eventService.getAllEvents());
        } else {
            model.addAttribute("events", eventService.getEventsByOrganizer(currentUser));
        }
        return "events/manage";
    }
}
