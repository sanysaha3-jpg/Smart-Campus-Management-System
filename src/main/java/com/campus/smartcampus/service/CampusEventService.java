package com.campus.smartcampus.service;

import com.campus.smartcampus.model.CampusEvent;
import com.campus.smartcampus.model.EventRegistration;
import com.campus.smartcampus.model.Role;
import com.campus.smartcampus.model.User;
import com.campus.smartcampus.repository.CampusEventRepository;
import com.campus.smartcampus.repository.EventRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampusEventService {

    private final CampusEventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    public CampusEventService(CampusEventRepository eventRepository,
                              EventRegistrationRepository registrationRepository,
                              NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    public CampusEvent createEvent(CampusEvent event, User organizer) {
        event.setOrganizer(organizer);
        event.setCurrentParticipants(0);
        event.setStatus("UPCOMING");
        CampusEvent saved = eventRepository.save(event);

        // Notify all users about new campus event
        notificationService.notifyAllUsers(
                "New Event: " + saved.getTitle(),
                "A new " + saved.getCategory() + " has been scheduled on " + saved.getEventDate() + " at " + saved.getVenue(),
                "/events/view/" + saved.getId(),
                "EVENT"
        );
        return saved;
    }

    public CampusEvent updateEvent(Long eventId, CampusEvent updated, User user) {
        CampusEvent event = getEventById(eventId);
        // Only organizer or admin can edit
        if (!event.getOrganizer().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("You are not authorized to edit this event.");
        }
        event.setTitle(updated.getTitle());
        event.setDescription(updated.getDescription());
        event.setCategory(updated.getCategory());
        event.setDepartment(updated.getDepartment());
        event.setVenue(updated.getVenue());
        event.setEventDate(updated.getEventDate());
        event.setStartTime(updated.getStartTime());
        event.setEndTime(updated.getEndTime());
        event.setMaxParticipants(updated.getMaxParticipants());
        if (updated.getBannerUrl() != null && !updated.getBannerUrl().isBlank()) {
            event.setBannerUrl(updated.getBannerUrl());
        }
        return eventRepository.save(event);
    }

    public void cancelEvent(Long eventId, User user) {
        CampusEvent event = getEventById(eventId);
        if (!event.getOrganizer().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("You are not authorized to cancel this event.");
        }
        event.setStatus("CANCELLED");
        eventRepository.save(event);

        // Notify registered participants
        List<EventRegistration> registrations = registrationRepository.findByEvent(event);
        for (EventRegistration reg : registrations) {
            notificationService.notifyUser(
                    reg.getUser(),
                    "Event Cancelled: " + event.getTitle(),
                    "The event scheduled for " + event.getEventDate() + " has been cancelled by the organizers.",
                    "/events",
                    "EVENT"
            );
        }
    }

    public CampusEvent getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
    }

    public List<CampusEvent> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    public List<CampusEvent> searchEvents(String keyword, String category, String department, LocalDate date) {
        return eventRepository.searchEvents(keyword, category, department, date);
    }

    public List<CampusEvent> getEventsByOrganizer(User organizer) {
        return eventRepository.findByOrganizerOrderByEventDateAsc(organizer);
    }

    public EventRegistration registerUserForEvent(Long eventId, User user) {
        CampusEvent event = getEventById(eventId);

        if ("CANCELLED".equalsIgnoreCase(event.getStatus())) {
            throw new IllegalStateException("Cannot register for a cancelled event.");
        }

        if (event.isFull()) {
            throw new IllegalStateException("Event has reached maximum capacity (" + event.getMaxParticipants() + ").");
        }

        Optional<EventRegistration> existing = registrationRepository.findByEventAndUser(event, user);
        if (existing.isPresent()) {
            EventRegistration reg = existing.get();
            if ("REGISTERED".equalsIgnoreCase(reg.getStatus())) {
                throw new IllegalStateException("You are already registered for this event.");
            } else {
                reg.setStatus("REGISTERED");
                event.setCurrentParticipants(event.getCurrentParticipants() + 1);
                eventRepository.save(event);
                return registrationRepository.save(reg);
            }
        }

        EventRegistration registration = new EventRegistration(event, user);
        event.setCurrentParticipants(event.getCurrentParticipants() + 1);
        eventRepository.save(event);
        EventRegistration saved = registrationRepository.save(registration);

        notificationService.notifyUser(
                user,
                "Registration Confirmed: " + event.getTitle(),
                "You have successfully registered for " + event.getTitle() + " on " + event.getEventDate() + " at " + event.getVenue() + ".",
                "/events/view/" + event.getId(),
                "EVENT"
        );
        return saved;
    }

    public void cancelRegistration(Long registrationId, User user) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found: " + registrationId));
        if (!registration.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Not authorized to cancel this registration.");
        }
        if ("REGISTERED".equalsIgnoreCase(registration.getStatus())) {
            registration.setStatus("CANCELLED");
            CampusEvent event = registration.getEvent();
            if (event.getCurrentParticipants() > 0) {
                event.setCurrentParticipants(event.getCurrentParticipants() - 1);
                eventRepository.save(event);
            }
            registrationRepository.save(registration);
        }
    }

    public List<EventRegistration> getUserRegistrations(User user) {
        return registrationRepository.findByUserOrderByRegistrationDateDesc(user);
    }

    public boolean isUserRegistered(CampusEvent event, User user) {
        return registrationRepository.existsByEventAndUserAndStatus(event, user, "REGISTERED");
    }

    public List<EventRegistration> getEventAttendees(Long eventId) {
        CampusEvent event = getEventById(eventId);
        return registrationRepository.findByEvent(event);
    }
}
