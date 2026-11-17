package com.campus.smartcampus.service;

import com.campus.smartcampus.model.Complaint;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final NotificationService notificationService;

    public ComplaintService(ComplaintRepository complaintRepository, NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.notificationService = notificationService;
    }

    public Complaint submitComplaint(Complaint complaint, User reporter) {
        complaint.setReportedBy(reporter);
        complaint.setStatus("PENDING");
        complaint.setCreatedAt(LocalDateTime.now());
        Complaint saved = complaintRepository.save(complaint);

        // Notify Admins
        notificationService.notifyUsersWithRole(
                Role.ADMIN,
                "New Maintenance Ticket #" + saved.getId() + " (" + saved.getPriority() + ")",
                saved.getTitle() + " reported at " + saved.getLocation(),
                "/complaints/view/" + saved.getId(),
                "COMPLAINT"
        );

        // Notify user of receipt
        notificationService.notifyUser(
                reporter,
                "Complaint Registered #" + saved.getId(),
                "Your issue '" + saved.getTitle() + "' has been logged and assigned to campus maintenance.",
                "/complaints/view/" + saved.getId(),
                "COMPLAINT"
        );

        return saved;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getUserComplaints(User user) {
        return complaintRepository.findByReportedByOrderByCreatedAtDesc(user);
    }

    public List<Complaint> filterComplaints(String status, String category, String priority) {
        return complaintRepository.filterComplaints(status, category, priority);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found: " + id));
    }

    public Complaint updateComplaintStatus(Long id, String status, String assignedDept, String adminRemarks) {
        Complaint complaint = getComplaintById(id);
        complaint.setStatus(status);
        if (assignedDept != null && !assignedDept.isBlank()) {
            complaint.setAssignedDepartment(assignedDept);
        }
        if (adminRemarks != null) {
            complaint.setAdminRemarks(adminRemarks);
        }
        if ("RESOLVED".equalsIgnoreCase(status)) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        Complaint updated = complaintRepository.save(complaint);

        // Notify student/reporter
        notificationService.notifyUser(
                complaint.getReportedBy(),
                "Maintenance Ticket #" + complaint.getId() + " Update: " + status,
                "Status: " + status + (adminRemarks != null && !adminRemarks.isBlank() ? " | Notes: " + adminRemarks : ""),
                "/complaints/view/" + complaint.getId(),
                "COMPLAINT"
        );

        return updated;
    }
}
