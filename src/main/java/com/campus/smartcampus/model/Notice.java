package com.campus.smartcampus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Notice title is required")
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank(message = "Notice content is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 100)
    private String department = "All Departments";

    @Column(name = "target_audience", length = 50)
    private String targetAudience = "ALL"; // ALL, STUDENTS, FACULTY, ORGANIZERS

    @Column(length = 50)
    private String category = "ACADEMIC"; // ACADEMIC, TIMETABLE, EVENT, EXAM, URGENT_ALERT, GENERAL

    @Column(nullable = false)
    private boolean urgent = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "published_by_id")
    private User publishedBy;

    @Column(name = "published_date")
    private LocalDateTime publishedDate = LocalDateTime.now();

    @Column(name = "expires_at")
    private LocalDate expiresAt;

    public Notice() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isUrgent() { return urgent; }
    public void setUrgent(boolean urgent) { this.urgent = urgent; }

    public User getPublishedBy() { return publishedBy; }
    public void setPublishedBy(User publishedBy) { this.publishedBy = publishedBy; }

    public LocalDateTime getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDateTime publishedDate) { this.publishedDate = publishedDate; }

    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
}
