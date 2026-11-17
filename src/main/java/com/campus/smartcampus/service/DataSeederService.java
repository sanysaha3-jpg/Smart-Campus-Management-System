package com.campus.smartcampus.service;

import com.campus.smartcampus.model.*;
import com.campus.smartcampus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataSeederService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CampusEventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final RoomRepository roomRepository;
    private final RoomBookingRepository bookingRepository;
    private final LostFoundItemRepository lostFoundRepository;
    private final ItemClaimRepository claimRepository;
    private final NoticeRepository noticeRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationRepository notificationRepository;
    private final CampusLocationRepository locationRepository;

    public DataSeederService(UserRepository userRepository,
                             CampusEventRepository eventRepository,
                             EventRegistrationRepository registrationRepository,
                             RoomRepository roomRepository,
                             RoomBookingRepository bookingRepository,
                             LostFoundItemRepository lostFoundRepository,
                             ItemClaimRepository claimRepository,
                             NoticeRepository noticeRepository,
                             ComplaintRepository complaintRepository,
                             NotificationRepository notificationRepository,
                             CampusLocationRepository locationRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.lostFoundRepository = lostFoundRepository;
        this.claimRepository = claimRepository;
        this.noticeRepository = noticeRepository;
        this.complaintRepository = complaintRepository;
        this.notificationRepository = notificationRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already seeded
        }

        System.out.println(">>> SEEDING SMART CAMPUS MANAGEMENT SYSTEM DEMO DATA <<<");

        // 1. Seed Users (Roles: ADMIN, FACULTY, ORGANIZER, STUDENT)
        User admin = userRepository.save(new User(
                "admin", "admin123", "Dr. Rajesh Sharma", "admin@campus.edu",
                "9876543210", Role.ADMIN, "Campus Administration", "EMP-001"
        ));

        User faculty = userRepository.save(new User(
                "faculty", "faculty123", "Prof. Ananya Sen", "ananya.sen@campus.edu",
                "9876543211", Role.FACULTY, "Computer Science & Engineering", "FAC-104"
        ));

        User organizer = userRepository.save(new User(
                "organizer", "organizer123", "Rohan Gupta", "rohan.events@campus.edu",
                "9876543212", Role.ORGANIZER, "Student Activity Council", "SAC-202"
        ));

        User student = userRepository.save(new User(
                "student", "student123", "Aarav Mehta", "aarav.mehta@campus.edu",
                "9876543213", Role.STUDENT, "Computer Science & Engineering", "CS-2023-042"
        ));

        User student2 = userRepository.save(new User(
                "priya", "student123", "Priya Patel", "priya.patel@campus.edu",
                "9876543214", Role.STUDENT, "Information Technology", "IT-2023-088"
        ));

        // 2. Seed Rooms
        Room r1 = roomRepository.save(new Room(
                "Room 101", "Aryabhatta Academic Block", "CLASSROOM", 60,
                "Projector, Smart Board, AC, High-Speed Wi-Fi", "Ground Floor"
        ));
        Room r2 = roomRepository.save(new Room(
                "Lab 204 (AI & Cloud)", "Turing Technology Complex", "LAB", 45,
                "45 High-End PCs, GPU Cluster, AC, Projector, Gigabit LAN", "2nd Floor"
        ));
        Room r3 = roomRepository.save(new Room(
                "Seminar Hall A", "Dr. APJ Abdul Kalam Complex", "SEMINAR_HALL", 150,
                "Acoustic Sound System, Dual Projectors, Podiums, Stage Lighting, AC", "1st Floor"
        ));
        Room r4 = roomRepository.save(new Room(
                "Study Room 3B", "Central Library", "STUDY_ROOM", 12,
                "Whiteboard, Power Outlets, High-Speed Wi-Fi, Quiet Zone", "3rd Floor"
        ));
        Room r5 = roomRepository.save(new Room(
                "Main Auditorium", "Dr. APJ Abdul Kalam Complex", "AUDITORIUM", 500,
                "Stage, Green Rooms, 4K Projection, Dolby Sound, Broadcast Setup", "Ground Floor"
        ));
        Room r6 = roomRepository.save(new Room(
                "Lab 105 (IoT & Embedded)", "Turing Technology Complex", "LAB", 40,
                "Oscilloscopes, Sensor Kits, Soldering Stations, AC", "1st Floor"
        ));

        // 3. Seed Existing Room Booking (to show conflict prevention in action)
        RoomBooking booking1 = new RoomBooking();
        booking1.setRoom(r3);
        booking1.setUser(faculty);
        booking1.setBookingDate(LocalDate.now().plusDays(2));
        booking1.setStartTime(LocalTime.of(14, 0));
        booking1.setEndTime(LocalTime.of(16, 0));
        booking1.setPurpose("Guest Lecture on Distributed Systems");
        booking1.setAttendeesCount(80);
        booking1.setStatus("CONFIRMED");
        bookingRepository.save(booking1);

        RoomBooking booking2 = new RoomBooking();
        booking2.setRoom(r4);
        booking2.setUser(student);
        booking2.setBookingDate(LocalDate.now().plusDays(1));
        booking2.setStartTime(LocalTime.of(10, 0));
        booking2.setEndTime(LocalTime.of(12, 0));
        booking2.setPurpose("Final Year Capstone Project Group Discussion");
        booking2.setAttendeesCount(6);
        booking2.setStatus("CONFIRMED");
        bookingRepository.save(booking2);

        // 4. Seed Campus Events
        CampusEvent e1 = new CampusEvent();
        e1.setTitle("HackCampus 2026 - 24hr National Hackathon");
        e1.setDescription("Build innovative solutions in Web3, AI, IoT, and FinTech. ₹1,50,000 prize pool, mentorship from industry experts, and internship opportunities!");
        e1.setCategory("HACKATHON");
        e1.setDepartment("Computer Science & Engineering");
        e1.setVenue("Turing Lab 204 & Seminar Hall A");
        e1.setEventDate(LocalDate.now().plusDays(7));
        e1.setStartTime(LocalTime.of(9, 0));
        e1.setEndTime(LocalTime.of(18, 0));
        e1.setMaxParticipants(120);
        e1.setCurrentParticipants(1);
        e1.setOrganizer(organizer);
        e1.setStatus("UPCOMING");
        e1.setBannerUrl("https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&q=80");
        eventRepository.save(e1);

        CampusEvent e2 = new CampusEvent();
        e2.setTitle("Workshop on PyTorch & Generative Deep Learning");
        e2.setDescription("Hands-on coding workshop covering neural network architectures, attention mechanisms, and training custom vision models.");
        e2.setCategory("WORKSHOP");
        e2.setDepartment("Information Technology");
        e2.setVenue("Lab 204 (AI & Cloud)");
        e2.setEventDate(LocalDate.now().plusDays(3));
        e2.setStartTime(LocalTime.of(10, 0));
        e2.setEndTime(LocalTime.of(13, 0));
        e2.setMaxParticipants(45);
        e2.setCurrentParticipants(1);
        e2.setOrganizer(faculty);
        e2.setStatus("UPCOMING");
        e2.setBannerUrl("https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&q=80");
        eventRepository.save(e2);

        CampusEvent e3 = new CampusEvent();
        e3.setTitle("Vibrance 2026 - Annual Inter-College Cultural Fest");
        e3.setDescription("Three days of electrifying music, dance battles, dramatics, fashion showcases, and celebrity musical performance!");
        e3.setCategory("FEST");
        e3.setDepartment("Student Activity Council");
        e3.setVenue("Main Auditorium & Campus Grounds");
        e3.setEventDate(LocalDate.now().plusDays(14));
        e3.setStartTime(LocalTime.of(11, 0));
        e3.setEndTime(LocalTime.of(21, 0));
        e3.setMaxParticipants(500);
        e3.setCurrentParticipants(0);
        e3.setOrganizer(organizer);
        e3.setStatus("UPCOMING");
        e3.setBannerUrl("https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=800&q=80");
        eventRepository.save(e3);

        CampusEvent e4 = new CampusEvent();
        e4.setTitle("Inter-Department Cricket & Badminton Cup");
        e4.setDescription("Represent your branch in the annual sports extravaganza! Trophies, medals, and best athlete honors to be awarded.");
        e4.setCategory("SPORTS");
        e4.setDepartment("Department of Physical Education");
        e4.setVenue("Main Sports Complex Ground & Indoor Stadium");
        e4.setEventDate(LocalDate.now().plusDays(10));
        e4.setStartTime(LocalTime.of(8, 0));
        e4.setEndTime(LocalTime.of(17, 0));
        e4.setMaxParticipants(150);
        e4.setCurrentParticipants(0);
        e4.setOrganizer(organizer);
        e4.setStatus("UPCOMING");
        e4.setBannerUrl("https://images.unsplash.com/photo-1531415074868-036b1c57e329?w=800&q=80");
        eventRepository.save(e4);

        // Event registrations for demo
        registrationRepository.save(new EventRegistration(e1, student));
        registrationRepository.save(new EventRegistration(e2, student));

        // 5. Seed Notices & Urgent Alerts
        Notice n1 = new Notice();
        n1.setTitle("Schedule for Mid-Semester Theory & Practical Exams");
        n1.setContent("The mid-term exams for all 2nd, 3rd, and 4th year undergraduate courses will commence from the 1st of next month. Download timetable from student portal.");
        n1.setDepartment("Examination Cell");
        n1.setCategory("EXAM");
        n1.setTargetAudience("STUDENTS");
        n1.setUrgent(false);
        n1.setPublishedBy(admin);
        n1.setExpiresAt(LocalDate.now().plusDays(30));
        noticeRepository.save(n1);

        Notice n2 = new Notice();
        n2.setTitle("🚨 URGENT: Campus Core Network & Wi-Fi Maintenance Tonight");
        n2.setContent("Campus IT cell will perform scheduled fiber optic backbone upgrades tonight between 11:00 PM and 3:00 AM. Internet and intranet services may face momentary downtime.");
        n2.setDepartment("IT Support Cell");
        n2.setCategory("URGENT_ALERT");
        n2.setTargetAudience("ALL");
        n2.setUrgent(true);
        n2.setPublishedBy(admin);
        n2.setExpiresAt(LocalDate.now().plusDays(2));
        noticeRepository.save(n2);

        Notice n3 = new Notice();
        n3.setTitle("Inviting Project Proposals for Smart India & Campus Hackathon 2026");
        n3.setContent("Faculty mentors and student teams interested in submitting proposals for university innovation grant funding are invited to submit abstracts before the 25th.");
        n3.setDepartment("Research & Innovation Center");
        n3.setCategory("ACADEMIC");
        n3.setTargetAudience("ALL");
        n3.setUrgent(false);
        n3.setPublishedBy(faculty);
        n3.setExpiresAt(LocalDate.now().plusDays(20));
        noticeRepository.save(n3);

        Notice n4 = new Notice();
        n4.setTitle("Timetable Revision for 3rd Year CS & IT Sections A & B");
        n4.setContent("Please note that Tuesday morning DBMS lab sessions have been rescheduled to Thursday afternoon slot (2:00 PM - 4:00 PM) in Turing Lab 204.");
        n4.setDepartment("Computer Science & Engineering");
        n4.setCategory("TIMETABLE");
        n4.setTargetAudience("STUDENTS");
        n4.setUrgent(false);
        n4.setPublishedBy(faculty);
        n4.setExpiresAt(LocalDate.now().plusDays(15));
        noticeRepository.save(n4);

        // 6. Seed Lost & Found Items
        LostFoundItem lf1 = new LostFoundItem();
        lf1.setTitle("Lost: Dell 65W Type-C Laptop Power Adapter");
        lf1.setDescription("Black Dell 65W USB-C charger left on the desk near window row in Central Library 2nd floor silent reading room.");
        lf1.setType("LOST");
        lf1.setCategory("ELECTRONICS");
        lf1.setLocation("Central Library 2nd Floor Reading Room");
        lf1.setItemDate(LocalDate.now().minusDays(1));
        lf1.setStatus("REPORTED");
        lf1.setReportedBy(student);
        lf1.setContactPhone("9876543213");
        lf1.setContactEmail("aarav.mehta@campus.edu");
        lostFoundRepository.save(lf1);

        LostFoundItem lf2 = new LostFoundItem();
        lf2.setTitle("Found: Silver Metal Fastrack Analog Wristwatch");
        lf2.setDescription("Found on the cafeteria billing counter table around 1:30 PM. Has a blue dial with stainless steel chain strap.");
        lf2.setType("FOUND");
        lf2.setCategory("OTHER");
        lf2.setLocation("Student Amenity Cafeteria Counter #2");
        lf2.setItemDate(LocalDate.now());
        lf2.setStatus("REPORTED");
        lf2.setReportedBy(student2);
        lf2.setContactPhone("9876543214");
        lf2.setContactEmail("priya.patel@campus.edu");
        lostFoundRepository.save(lf2);

        LostFoundItem lf3 = new LostFoundItem();
        lf3.setTitle("Found: Student ID Card & Metro Pass in Blue Holder");
        lf3.setDescription("Belongs to CS 2nd year student. Found near staircase of Aryabhatta Block.");
        lf3.setType("FOUND");
        lf3.setCategory("ID_CARD");
        lf3.setLocation("Aryabhatta Academic Block Stairs");
        lf3.setItemDate(LocalDate.now().minusDays(2));
        lf3.setStatus("REPORTED");
        lf3.setReportedBy(faculty);
        lf3.setContactPhone("9876543211");
        lf3.setContactEmail("ananya.sen@campus.edu");
        lostFoundRepository.save(lf3);

        // 7. Seed Maintenance Complaints
        Complaint c1 = new Complaint();
        c1.setTitle("AC unit blowing warm air in Lab 204");
        c1.setDescription("Rear split AC unit #2 in Turing Lab 204 is not cooling properly and making a buzzing noise during lab hours.");
        c1.setCategory("ELECTRICAL");
        c1.setLocation("Turing Technology Complex Lab 204");
        c1.setPriority("HIGH");
        c1.setStatus("IN_PROGRESS");
        c1.setReportedBy(faculty);
        c1.setAssignedDepartment("Electrical & HVAC Wing");
        c1.setAdminRemarks("Technician technician assigned; gas pressure check in progress.");
        complaintRepository.save(c1);

        Complaint c2 = new Complaint();
        c2.setTitle("Wi-Fi Access Point unreachable near Library Study Pods");
        c2.setDescription("The Wi-Fi SSID 'Campus-Student-5G' connects with 'No Internet' error on 3rd floor study section.");
        c2.setCategory("WIFI_NETWORK");
        c2.setLocation("Central Library 3rd Floor");
        c2.setPriority("MEDIUM");
        c2.setStatus("PENDING");
        c2.setReportedBy(student);
        c2.setAssignedDepartment("IT Support Team");
        complaintRepository.save(c2);

        Complaint c3 = new Complaint();
        c3.setTitle("Water cooler filter tap leaking continuously");
        c3.setDescription("Water purifier on ground floor corridor next to Room 101 has dripping tap causing water accumulation.");
        c3.setCategory("PLUMBING");
        c3.setLocation("Aryabhatta Block Ground Floor");
        c3.setPriority("URGENT");
        c3.setStatus("RESOLVED");
        c3.setReportedBy(student2);
        c3.setAssignedDepartment("Estate & Plumbing Wing");
        c3.setAdminRemarks("Tap washer replaced and drainage cleared by plumbing staff.");
        c3.setResolvedAt(LocalDateTime.now().minusHours(4));
        complaintRepository.save(c3);

        // 8. Seed Campus Directory & Navigation
        locationRepository.save(new CampusLocation(
                "Central Library & Digital Resource Center", "Central Library", "Ground to 3rd Floor",
                "LIBRARY", "08:00 AM - 10:00 PM", "library@campus.edu", "011-2345-6701",
                "Houses over 100,000 volumes, IEEE & ACM digital libraries, quiet study carrels, and computer research stations.",
                "bi-book", "Zone B", 35, 40
        ));

        locationRepository.save(new CampusLocation(
                "Turing Technology Complex (CS & IT)", "Turing Block", "Ground to 4th Floor",
                "ACADEMIC", "08:30 AM - 07:00 PM", "cse@campus.edu", "011-2345-6702",
                "State-of-the-art computer labs, AI research center, IoT prototyping center, and seminar rooms.",
                "bi-cpu", "Zone A", 70, 30
        ));

        locationRepository.save(new CampusLocation(
                "Aryabhatta Academic Block (ECE & Mech)", "Aryabhatta Block", "Ground to 3rd Floor",
                "ACADEMIC", "08:30 AM - 06:00 PM", "aryabhatta@campus.edu", "011-2345-6703",
                "Smart classrooms, robotics arena, hardware labs, and faculty advisory chambers.",
                "bi-building", "Zone A", 20, 25
        ));

        locationRepository.save(new CampusLocation(
                "Campus Central Cafeteria & Food Court", "Student Amenity Center", "Ground Floor",
                "CAFETERIA", "07:30 AM - 09:30 PM", "cafeteria@campus.edu", "011-2345-6705",
                "Multi-cuisine food court serving hygienic breakfast, lunch, hot beverages, and snacks with outdoor seating.",
                "bi-cup-hot", "Zone C", 45, 75
        ));

        locationRepository.save(new CampusLocation(
                "Dr. APJ Abdul Kalam Auditorium", "Main Auditorium", "Ground Floor",
                "ACADEMIC", "09:00 AM - 08:00 PM", "auditorium@campus.edu", "011-2345-6706",
                "500-seater air-conditioned auditorium with high-definition audio/visual projection for convocations and cultural fests.",
                "bi-camera-reels", "Zone B", 80, 65
        ));

        locationRepository.save(new CampusLocation(
                "Campus Medical & Emergency Health Center", "Student Amenity Center", "1st Floor",
                "HEALTH_CENTER", "24x7 Emergency / OPD 9 AM - 5 PM", "health@campus.edu", "011-2345-6709",
                "Round-the-clock nursing staff, resident medical officer, first-aid dispensary, and ambulance facility.",
                "bi-heart-pulse", "Zone C", 55, 80
        ));

        locationRepository.save(new CampusLocation(
                "Administrative Registrar & Accounts Office", "Admin Block", "1st Floor",
                "ADMIN_OFFICE", "09:00 AM - 05:00 PM", "admin@campus.edu", "011-2345-6700",
                "Student fees, admissions, scholarship verifications, hall tickets, and official certificates counter.",
                "bi-briefcase", "Zone A", 15, 60
        ));

        locationRepository.save(new CampusLocation(
                "Olympic Sports Complex & Gymnasium", "Sports Pavilion", "Ground Floor",
                "SPORTS", "06:00 AM - 08:30 PM", "sports@campus.edu", "011-2345-6708",
                "Olympic-size athletic track, football ground, basketball and tennis courts, and fitness gymnasium.",
                "bi-trophy", "Zone D", 85, 20
        ));

        // 9. Seed Initial Notifications
        notificationRepository.save(new Notification(
                student, "Welcome to Smart Campus!",
                "Explore upcoming hackathons, book study rooms, check live notices, and access campus services from your portal.",
                "/dashboard", "GENERAL"
        ));
        notificationRepository.save(new Notification(
                student, "Registration Confirmed: HackCampus 2026",
                "You are confirmed for HackCampus 2026 starting on " + LocalDate.now().plusDays(7) + ".",
                "/events/my-registrations", "EVENT"
        ));

        System.out.println(">>> SEEDING COMPLETED SUCCESSFULLY! <<<");
    }
}
