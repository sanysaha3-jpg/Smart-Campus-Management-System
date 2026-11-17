package com.campus.smartcampus.controller;

import com.campus.smartcampus.model.ItemClaim;
import com.campus.smartcampus.model.LostFoundItem;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.LostFoundService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/lost-found")
public class LostFoundController {

    private final LostFoundService lostFoundService;

    public LostFoundController(LostFoundService lostFoundService) {
        this.lostFoundService = lostFoundService;
    }

    @GetMapping
    public String listItems(@RequestParam(value = "type", required = false) String type,
                            @RequestParam(value = "category", required = false) String category,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            @RequestParam(value = "status", required = false) String status,
                            Model model) {
        model.addAttribute("items", lostFoundService.searchItems(type, category, keyword, status));
        model.addAttribute("type", type);
        model.addAttribute("category", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        return "lost-found/list";
    }

    @GetMapping("/report")
    public String showReportForm(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        LostFoundItem item = new LostFoundItem();
        item.setItemDate(LocalDate.now());
        item.setContactPhone(currentUser.getPhone());
        item.setContactEmail(currentUser.getEmail());
        model.addAttribute("item", item);
        return "lost-found/report";
    }

    @PostMapping("/report")
    public String submitReport(@ModelAttribute("item") LostFoundItem item,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            LostFoundItem saved = lostFoundService.reportItem(item, currentUser);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your " + saved.getType().toLowerCase() + " item report has been published!");
            return "redirect:/lost-found/view/" + saved.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error reporting item: " + e.getMessage());
            return "redirect:/lost-found/report";
        }
    }

    @GetMapping("/view/{id}")
    public String viewItem(@PathVariable("id") Long id, HttpSession session, Model model) {
        LostFoundItem item = lostFoundService.getItemById(id);
        model.addAttribute("item", item);

        User currentUser = (User) session.getAttribute("currentUser");
        boolean isOwner = false;
        boolean canVerify = false;

        if (currentUser != null) {
            isOwner = item.getReportedBy().getId().equals(currentUser.getId());
            canVerify = isOwner || currentUser.getRole() == Role.ADMIN;
            List<ItemClaim> claims = lostFoundService.getClaimsForItem(id);
            model.addAttribute("claims", claims);
        }
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("canVerify", canVerify);

        return "lost-found/view";
    }

    @PostMapping("/claim/{id}")
    public String submitClaim(@PathVariable("id") Long id,
                              @RequestParam("proofDescription") String proofDescription,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            lostFoundService.submitClaim(id, currentUser, proofDescription);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Your ownership claim has been submitted to the finder and campus admin for verification!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/lost-found/view/" + id;
    }

    @PostMapping("/claim/verify/{claimId}")
    public String verifyClaim(@PathVariable("claimId") Long claimId,
                              @RequestParam("status") String status,
                              @RequestParam(value = "adminNotes", required = false) String adminNotes,
                              @RequestParam("itemId") Long itemId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            lostFoundService.verifyClaim(claimId, status, adminNotes, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Claim status updated to: " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/lost-found/view/" + itemId;
    }

    @PostMapping("/resolve/{id}")
    public String markResolved(@PathVariable("id") Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            lostFoundService.markAsResolved(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Item marked as resolved / returned!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/lost-found/view/" + id;
    }

    @GetMapping("/my-items")
    public String myItems(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        model.addAttribute("myReportedItems", lostFoundService.getItemsReportedByUser(currentUser));
        model.addAttribute("myClaims", lostFoundService.getUserClaims(currentUser));
        return "lost-found/my-items";
    }
}
