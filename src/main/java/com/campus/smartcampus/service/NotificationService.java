package com.campus.smartcampus.service;

import com.campus.smartcampus.model.Notification;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.NotificationRepository;
import com.campus.smartcampus.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification notifyUser(User user, String title, String message, String link, String type) {
        Notification notification = new Notification(user, title, message, link, type);
        return notificationRepository.save(notification);
    }

    public void notifyAllUsers(String title, String message, String link, String type) {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            notifyUser(user, title, message, link, type);
        }
    }

    public void notifyUsersWithRole(Role role, String title, String message, String link, String type) {
        List<User> users = userRepository.findByRole(role);
        for (User user : users) {
            notifyUser(user, title, message, link, type);
        }
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findTop10ByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getAllUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    public void markAllAsRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(user);
        for (Notification n : unread) {
            n.setRead(true);
        }
        notificationRepository.saveAll(unread);
    }
}
