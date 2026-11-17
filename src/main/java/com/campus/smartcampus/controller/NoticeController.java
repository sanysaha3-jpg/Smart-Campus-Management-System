package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.Notice;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.NoticeService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public String listNotices(@RequestParam(value = "department", required = false) String department,
                              @RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "audience", required = false) String audience,
                              Model model) {
        model.addAttribute("notices", noticeService.filterNotices(department, category, audience));
        model.addAttribute("urgentNotices", noticeService.getUrgentNotices());
        model.addAttribute("selectedDept", department);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedAudience", audience);
        return "notices/list";
    }

    @GetMapping("/create")
    public String showCreateForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.FACULTY)) {
            return "redirect:/notices?error=Unauthorized";
        }
        Notice notice = new Notice();
        notice.setDepartment(currentUser.getDepartment());
        notice.setExpiresAt(LocalDate.now().plusDays(30));
        model.addAttribute("notice", notice);
        return "notices/create";
    }

    @PostMapping("/create")
    public String createNotice(@ModelAttribute("notice") Notice notice,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.FACULTY)) {
            return "redirect:/notices?error=Unauthorized";
        }
        try {
            Notice saved = noticeService.createNotice(notice, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Notice '" + saved.getTitle() + "' published successfully!");
            return "redirect:/notices";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error publishing notice: " + e.getMessage());
            return "redirect:/notices/create";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteNotice(@PathVariable("id") Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";
        try {
            noticeService.deleteNotice(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Notice deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/notices";
    }
}
