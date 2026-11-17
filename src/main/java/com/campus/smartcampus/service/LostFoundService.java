package com.campus.smartcampus.service;

import com.campus.smartcampus.model.ItemClaim;
import com.campus.smartcampus.model.LostFoundItem;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.ItemClaimRepository;
import com.campus.smartcampus.repository.LostFoundItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LostFoundService {

    private final LostFoundItemRepository itemRepository;
    private final ItemClaimRepository claimRepository;
    private final NotificationService notificationService;

    public LostFoundService(LostFoundItemRepository itemRepository,
                            ItemClaimRepository claimRepository,
                            NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.claimRepository = claimRepository;
        this.notificationService = notificationService;
    }

    public LostFoundItem reportItem(LostFoundItem item, User reporter) {
        item.setReportedBy(reporter);
        item.setStatus("REPORTED");
        LostFoundItem saved = itemRepository.save(item);

        // Notify admins/campus
        notificationService.notifyUsersWithRole(
                Role.ADMIN,
                "New " + saved.getType() + " Item Reported: " + saved.getTitle(),
                "A " + saved.getType().toLowerCase() + " item (" + saved.getCategory() + ") was reported at " + saved.getLocation(),
                "/lost-found/view/" + saved.getId(),
                "CLAIM"
        );
        return saved;
    }

    public List<LostFoundItem> getAllItems() {
        return itemRepository.findAllByOrderByItemDateDesc();
    }

    public List<LostFoundItem> getItemsByType(String type) {
        return itemRepository.findByTypeOrderByItemDateDesc(type);
    }

    public List<LostFoundItem> searchItems(String type, String category, String keyword, String status) {
        return itemRepository.searchItems(type, category, keyword, status);
    }

    public LostFoundItem getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + id));
    }

    public List<LostFoundItem> getItemsReportedByUser(User user) {
        return itemRepository.findByReportedByOrderByItemDateDesc(user);
    }

    public ItemClaim submitClaim(Long itemId, User claimant, String proofDescription) {
        LostFoundItem item = getItemById(itemId);

        if (item.getReportedBy().getId().equals(claimant.getId())) {
            throw new IllegalStateException("You cannot claim an item you reported yourself.");
        }

        if (claimRepository.existsByItemAndClaimant(item, claimant)) {
            throw new IllegalStateException("You have already submitted an ownership claim for this item.");
        }

        ItemClaim claim = new ItemClaim(item, claimant, proofDescription);
        ItemClaim saved = claimRepository.save(claim);

        item.setStatus("CLAIMED");
        itemRepository.save(item);

        // Notify the person who reported the found item
        notificationService.notifyUser(
                item.getReportedBy(),
                "Ownership Claim Submitted: " + item.getTitle(),
                claimant.getFullName() + " has submitted an ownership claim for the item you reported.",
                "/lost-found/view/" + item.getId(),
                "CLAIM"
        );

        // Notify admins
        notificationService.notifyUsersWithRole(
                Role.ADMIN,
                "New Claim on: " + item.getTitle(),
                claimant.getFullName() + " filed a claim for: " + item.getTitle(),
                "/lost-found/view/" + item.getId(),
                "CLAIM"
        );

        return saved;
    }

    public List<ItemClaim> getClaimsForItem(Long itemId) {
        LostFoundItem item = getItemById(itemId);
        return claimRepository.findByItemOrderByClaimDateDesc(item);
    }

    public List<ItemClaim> getUserClaims(User user) {
        return claimRepository.findByClaimantOrderByClaimDateDesc(user);
    }

    public void verifyClaim(Long claimId, String status, String adminNotes, User actor) {
        ItemClaim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new IllegalArgumentException("Claim not found: " + claimId));

        LostFoundItem item = claim.getItem();

        // Check permission: admin or the original reporter
        if (actor.getRole() != Role.ADMIN && !item.getReportedBy().getId().equals(actor.getId())) {
            throw new IllegalStateException("Unauthorized to verify this claim.");
        }

        claim.setStatus(status); // APPROVED or REJECTED
        claim.setAdminNotes(adminNotes);
        claimRepository.save(claim);

        if ("APPROVED".equalsIgnoreCase(status)) {
            item.setStatus("RESOLVED");
            itemRepository.save(item);

            notificationService.notifyUser(
                    claim.getClaimant(),
                    "Claim Approved! " + item.getTitle(),
                    "Your claim for '" + item.getTitle() + "' has been APPROVED! Please visit the campus lost & found center or contact " + item.getContactPhone() + " to collect your item.",
                    "/lost-found/view/" + item.getId(),
                    "CLAIM"
            );
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            // Check if there are other pending claims
            List<ItemClaim> others = claimRepository.findByItemOrderByClaimDateDesc(item);
            boolean hasPending = others.stream().anyMatch(c -> "PENDING".equalsIgnoreCase(c.getStatus()) && !c.getId().equals(claimId));
            if (!hasPending) {
                item.setStatus("REPORTED");
                itemRepository.save(item);
            }

            notificationService.notifyUser(
                    claim.getClaimant(),
                    "Claim Update: " + item.getTitle(),
                    "Your claim for '" + item.getTitle() + "' could not be verified. Note: " + adminNotes,
                    "/lost-found/view/" + item.getId(),
                    "CLAIM"
            );
        }
    }

    public void markAsResolved(Long itemId, User actor) {
        LostFoundItem item = getItemById(itemId);
        if (actor.getRole() != Role.ADMIN && !item.getReportedBy().getId().equals(actor.getId())) {
            throw new IllegalStateException("Unauthorized to mark this item as resolved.");
        }
        item.setStatus("RESOLVED");
        itemRepository.save(item);
    }
}
