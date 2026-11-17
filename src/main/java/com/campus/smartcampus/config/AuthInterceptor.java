package com.campus.smartcampus.config;

import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final NotificationService notificationService;

    public AuthInterceptor(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        // Public routes
        if (isPublicPath(path)) {
            return true;
        }

        // Must be logged in
        if (currentUser == null) {
            response.sendRedirect("/login?error=" + java.net.URLEncoder.encode("Please log in to access this page.", java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }

        // Role-based authorization checks
        if (path.startsWith("/admin") && currentUser.getRole() != Role.ADMIN) {
            response.sendRedirect("/dashboard?error=" + java.net.URLEncoder.encode("Access Denied: Admin privileges required.", java.nio.charset.StandardCharsets.UTF_8));
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && modelAndView.getViewName() != null && !modelAndView.getViewName().startsWith("redirect:")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                User currentUser = (User) session.getAttribute("currentUser");
                if (currentUser != null) {
                    modelAndView.addObject("currentUser", currentUser);
                    long unreadCount = notificationService.getUnreadCount(currentUser);
                    modelAndView.addObject("unreadCount", unreadCount);
                }
            }
        }
    }

    private boolean isPublicPath(String path) {
        return path.equals("/") ||
               path.equals("/login") ||
               path.equals("/register") ||
               path.equals("/logout") ||
               path.equals("/directory") ||
               path.startsWith("/directory/view/") ||
               path.equals("/events") ||
               path.startsWith("/events/view/") ||
               path.equals("/notices") ||
               path.equals("/rooms") ||
               path.equals("/lost-found") ||
               path.startsWith("/lost-found/view/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/h2-console");
    }
}
