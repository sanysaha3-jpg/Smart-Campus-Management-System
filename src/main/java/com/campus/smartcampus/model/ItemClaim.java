package com.campus.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_claims")
public class ItemClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private LostFoundItem item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @NotBlank(message = "Proof description is required")
    @Column(name = "proof_description", columnDefinition = "TEXT", nullable = false)
    private String proofDescription;

    @Column(length = 30)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "admin_notes", length = 255)
    private String adminNotes;

    @Column(name = "claim_date")
    private LocalDateTime claimDate = LocalDateTime.now();

    public ItemClaim() {}

    public ItemClaim(LostFoundItem item, User claimant, String proofDescription) {
        this.item = item;
        this.claimant = claimant;
        this.proofDescription = proofDescription;
        this.status = "PENDING";
        this.claimDate = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LostFoundItem getItem() { return item; }
    public void setItem(LostFoundItem item) { this.item = item; }

    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }

    public String getProofDescription() { return proofDescription; }
    public void setProofDescription(String proofDescription) { this.proofDescription = proofDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }

    public LocalDateTime getClaimDate() { return claimDate; }
    public void setClaimDate(LocalDateTime claimDate) { this.claimDate = claimDate; }
}
