package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.Complaint;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.ComplaintService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping
    public String listComplaints(@RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "category", required = false) String category,
                                 @RequestParam(value = "priority", required = false) String priority,
                                 HttpSession session,
                                 Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        if (currentUser.getRole() == Role.ADMIN) {
            model.addAttribute("complaints", complaintService.filterComplaints(status, category, priority));
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("complaints", complaintService.getUserComplaints(currentUser));
            model.addAttribute("isAdmin", false);
        }
        model.addAttribute("status", status);
        model.addAttribute("category", category);
        model.addAttribute("priority", priority);
        return "complaints/list";
    }

    @GetMapping("/report")
    public String showReportForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("complaint", new Complaint());
        return "complaints/report";
    }

    @PostMapping("/report")
    public String submitComplaint(@ModelAttribute("complaint") Complaint complaint,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            Complaint saved = complaintService.submitComplaint(complaint, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Complaint #" + saved.getId() + " registered successfully! Our maintenance cell will attend to it.");
            return "redirect:/complaints/view/" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting complaint: " + e.getMessage());
            return "redirect:/complaints/report";
        }
    }

    @GetMapping("/view/{id}")
    public String viewComplaint(@PathVariable("id") Long id, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Complaint complaint = complaintService.getComplaintById(id);
        if (currentUser.getRole() != Role.ADMIN && !complaint.getReportedBy().getId().equals(currentUser.getId())) {
            return "redirect:/complaints?error=Unauthorized";
        }

        model.addAttribute("complaint", complaint);
        model.addAttribute("isAdmin", currentUser.getRole() == Role.ADMIN);
        return "complaints/view";
    }

    @PostMapping("/update/{id}")
    public String updateStatus(@PathVariable("id") Long id,
                               @RequestParam("status") String status,
                               @RequestParam(value = "assignedDepartment", required = false) String assignedDepartment,
                               @RequestParam(value = "adminRemarks", required = false) String adminRemarks,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            return "redirect:/complaints?error=Unauthorized";
        }

        try {
            complaintService.updateComplaintStatus(id, status, assignedDepartment, adminRemarks);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint #" + id + " updated to " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating complaint: " + e.getMessage());
        }
        return "redirect:/complaints/view/" + id;
    }

    @GetMapping("/my-complaints")
    public String myComplaints(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("complaints", complaintService.getUserComplaints(currentUser));
        return "complaints/my-complaints";
    }
}
