package com.campus.smartcampus.service;

import com.campus.smartcampus.model.Notice;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NotificationService notificationService;

    public NoticeService(NoticeRepository noticeRepository, NotificationService notificationService) {
        this.noticeRepository = noticeRepository;
        this.notificationService = notificationService;
    }

    public Notice createNotice(Notice notice, User publisher) {
        notice.setPublishedBy(publisher);
        notice.setPublishedDate(LocalDateTime.now());
        Notice saved = noticeRepository.save(notice);

        // If urgent, send notification to everyone immediately
        if (saved.isUrgent()) {
            notificationService.notifyAllUsers(
                    "🚨 URGENT CAMPUS ALERT: " + saved.getTitle(),
                    saved.getContent(),
                    "/notices",
                    "NOTICE"
            );
        } else {
            // General notice notification
            notificationService.notifyAllUsers(
                    "New Notice: " + saved.getTitle(),
                    "Published by " + publisher.getFullName() + " for " + saved.getDepartment(),
                    "/notices",
                    "NOTICE"
            );
        }
        return saved;
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByPublishedDateDesc();
    }

    public List<Notice> getUrgentNotices() {
        return noticeRepository.findByUrgentTrueOrderByPublishedDateDesc();
    }

    public List<Notice> filterNotices(String department, String category, String audience) {
        return noticeRepository.filterNotices(department, category, audience);
    }

    public Notice getNoticeById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + id));
    }

    public void deleteNotice(Long id, User user) {
        Notice notice = getNoticeById(id);
        if (user.getRole() != Role.ADMIN && !notice.getPublishedBy().getId().equals(user.getId())) {
            throw new IllegalStateException("Unauthorized to delete this announcement.");
        }
        noticeRepository.delete(notice);
    }
}
