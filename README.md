# 🎓 Smart Campus Management System

A comprehensive, full-stack college campus management web application built with **Java (JDK 21/22)**, **Spring Boot 3.3.5**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, and **MySQL 8.0** (with automatic zero-config **H2 In-Memory** fallback).

---

## 🚀 One-Click Run Instructions

### Option 1: Double-Click Batch File (Recommended for Windows)
1. Double-click **`run.bat`** in the project folder.
2. The script detects Java, launches the pre-built Spring Boot executable JAR, and automatically opens your browser at:
   👉 **`http://localhost:8080/`**

### Option 2: Run via Command Line
```bash
# Using the pre-built standalone executable JAR
java -jar target\smart-campus-1.0.0.jar

# Or using Maven Wrapper
.\mvnw.cmd spring-boot:run
```

### Option 3: Run with H2 In-Memory Mode (No MySQL Required)
If running on a computer without MySQL installed:
- Double-click **`run-h2.bat`**, or run:
```bash
java -jar target\smart-campus-1.0.0.jar --spring.profiles.active=h2
```

---

## 🔑 Demo Login Accounts

The system automatically initializes and seeds realistic demo data on first startup. You can also use the **One-Click Demo Login** buttons on the login page!

| Role | Username | Password | Full Name | Primary Capabilities |
| :--- | :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `admin123` | Dr. Rajesh Sharma | System metrics, user management & roles, room inventory, review tickets & claims |
| **Student** | `student` | `student123` | Aarav Mehta | Register for events, reserve study rooms, file complaints, report lost & found |
| **Faculty** | `faculty` | `faculty123` | Prof. Ananya Sen | Publish announcements, book seminar halls, review departmental maintenance |
| **Organizer** | `organizer` | `organizer123` | Rohan Gupta | Create campus hackathons & cultural fests, manage attendee rosters |
| **Student 2** | `priya` | `student123` | Priya Patel | Second student account for multi-user testing (e.g. claims, registrations) |

---

## 📦 Implemented Modules (All 8 Modules)

### 1. User Authentication & Role-Based Access Control
- **Roles:** `STUDENT`, `FACULTY`, `ORGANIZER`, `ADMIN`.
- Session-based authentication via `AuthInterceptor`.
- Role-restricted paths and dynamic navigation bar tailored to user privileges.
- User profile management (update email, phone, branch, student/faculty ID, password).
- Admin User Management panel (`/admin/users`) to toggle accounts (Active/Disabled) and change roles dynamically.

### 2. Campus Events Management
- Discover upcoming fests, technical workshops, 24hr hackathons, and sports tournaments.
- Multi-criteria search and filtering by keyword, event category, department, and date.
- Real-time seat tracking with visual capacity progress bars.
- 1-Click event registration and self-service registration cancellation.
- Organizers & Admins can create new events, edit event details, cancel events, and view/download attendee rosters.

### 3. Smart Room Finder & Reservation (With Overlap Prevention)
- Check available classrooms, computer labs, seminar halls, study spaces, and auditoriums.
- Filter by academic building, room type, minimum seating capacity, and facility amenities (Projector, Smart Board, AC, High-Speed Wi-Fi).
- **Intelligent Overlap Prevention Algorithm:**
  ```sql
  SELECT b FROM RoomBooking b WHERE b.room.id = :roomId 
    AND b.bookingDate = :bookingDate 
    AND b.status IN ('CONFIRMED', 'PENDING') 
    AND (:startTime < b.endTime AND :endTime > b.startTime)
  ```
  Prevents double-booking and informs the user of the conflicting schedule slot.
- My Bookings dashboard with cancellation support.
- Admin / Faculty room inventory management and booking status oversight.

### 4. Campus Lost & Found Registry
- Report lost belongings or items found across academic blocks and cafeterias.
- Fields: Title, description, type (`LOST`/`FOUND`), category, exact location, date, contact details, photo URL.
- Search and filter items by type, category, status (`REPORTED`, `CLAIMED`, `RESOLVED`), and keyword.
- **Ownership Claim Workflow:**
  - Students can submit ownership claims with specific proof/identifying marks.
  - The finder or campus administrator can review submitted claims and **Approve** or **Reject** them.
  - Approved items are automatically marked as `RESOLVED` / Returned and claimant is notified.

### 5. Official Notices & Urgent Announcements
- Campus digital bulletin board categorized by `ACADEMIC`, `EXAM`, `TIMETABLE`, `EVENT`, and `URGENT_ALERT`.
- Target audience segmentation: `ALL`, `STUDENTS`, `FACULTY`.
- **🚨 Urgent Emergency Alert System:** Urgent alerts trigger a high-visibility animated broadcast banner on every page and generate instant push notifications.
- Faculty & Admins can publish new notices with expiry dates and manage publications.

### 6. Campus Maintenance & Complaint Ticketing System
- Report broken equipment, Wi-Fi connectivity outages, air conditioning faults, or hygiene issues.
- Ticket classification: Category, Location, Priority (`LOW`, `MEDIUM`, `HIGH`, `URGENT`), detailed symptoms.
- Real-time status lifecycle tracking: `PENDING` ➔ `IN_PROGRESS` ➔ `RESOLVED`.
- Administrative management: assign responsible campus department (IT Support, Electrical, Estate/Plumbing) and log resolution remarks visible to the student.

### 7. Real-Time Notification Center & Reminders
- In-app notification bell dropdown in navigation bar with unread count badge.
- Automatic notifications dispatched on:
  - Event registration confirmation & event cancellations
  - Room booking confirmations & status changes
  - Maintenance ticket status updates and technician remarks
  - Lost & found ownership claim approvals and submissions
  - Urgent campus announcements
- Notification center page (`/notifications`) with 1-click "Mark All Read" action and direct link navigation.

### 8. Campus Directory & Interactive Aerial Navigation
- Searchable directory of academic blocks, computer research labs, central library, cafeterias, administrative offices, sports pavilions, and 24x7 medical health center.
- Filter by block, facility category, or keyword.
- **Interactive Visual Campus Map:** Custom coordinate-based aerial grid map with interactive building pins. Clicking any pin displays an instant overview card with floor information, operating hours, phone extensions, and direct links.

---

## 🛠️ Technology Stack & Architecture

- **Language:** Java (JDK 21/22)
- **Framework:** Spring Boot 3.3.5
- **Web Layer:** Spring MVC with Thymeleaf 3
- **ORM / Persistence:** Spring Data JPA with Hibernate 6
- **Database:** MySQL 8.0 (with connection pooling via HikariCP) + H2 in-memory profile
- **Styling & UI:** Bootstrap 5.3, Bootstrap Icons, Google Inter Font, Responsive CSS3
- **Build Tool:** Apache Maven 3.10 with included Maven Wrapper (`mvnw.cmd`)

---

## 📂 Project Directory Structure

```
smart-campus/
├── pom.xml                               # Maven project dependencies & build configuration
├── mvnw & mvnw.cmd                       # Maven Wrapper executables
├── run.bat                               # Windows 1-click launch script (MySQL)
├── run-h2.bat                            # Windows 1-click launch script (H2 In-Memory)
├── database_setup.sql                    # Full MySQL database export (schema + seed data)
├── src/
│   ├── main/
│   │   ├── java/com/campus/smartcampus/
│   │   │   ├── SmartCampusApplication.java
│   │   │   ├── config/                   # WebConfig, AuthInterceptor
│   │   │   ├── model/                    # JPA Entities (User, Event, Room, Booking, LostFound, etc.)
│   │   │   ├── repository/               # Spring Data JPA Repository interfaces
│   │   │   ├── service/                  # Business logic services & DataSeederService
│   │   │   └── controller/               # Spring MVC Controllers
│   │   └── resources/
│   │       ├── application.properties    # MySQL configuration (localhost:3306, user: root)
│   │       ├── application-h2.properties # Fallback H2 in-memory configuration
│   │       ├── static/
│   │       │   ├── css/style.css         # Theme styles & campus map canvas
│   │       │   └── js/main.js            # Auto-dismiss alerts, demo fill, map pin popups
│   │       └── templates/                # Thymeleaf HTML Templates
│   │           ├── index.html            # Landing page
│   │           ├── login.html            # 1-Click demo login
│   │           ├── register.html         # User sign up
│   │           ├── dashboard.html        # Role-based personalized dashboard
│   │           ├── profile.html          # Profile settings
│   │           ├── events/               # list, view, create, edit, my-registrations, manage
│   │           ├── rooms/                # list, book, my-bookings, manage
│   │           ├── lost-found/           # list, report, view, my-items
│   │           ├── notices/              # list, create
│   │           ├── complaints/           # list, report, view, my-complaints
│   │           ├── directory/            # index (interactive map), view
│   │           ├── notifications/        # list
│   │           ├── admin/                # users, overview
│   │           └── fragments/            # header, navbar, footer, alerts
│   └── test/                             # Unit & Integration Tests
└── target/
    └── smart-campus-1.0.0.jar            # Pre-compiled standalone executable JAR
```

---

## 🎓 College Viva / Project Evaluation Q&A

**Q: How does the system prevent overlapping room reservations?**  
*A: The `RoomBookingRepository` uses an overlap query: `(:newStart < existingEnd AND :newEnd > existingStart)`. If any confirmed booking matches the room on the requested date within that interval, the booking service throws a validation exception and redirects the user with an alert highlighting the conflicting booking.*

**Q: Does the project support different roles?**  
*A: Yes, four roles are implemented: `STUDENT`, `FACULTY`, `ORGANIZER`, and `ADMIN`. Each role sees a tailored navigation bar and dashboard, with route-level security enforced by `AuthInterceptor`.*

**Q: What happens if MySQL is not running on the evaluator's machine?**  
*A: The project includes an automatic H2 fallback profile. Running `run-h2.bat` launches the complete application with zero database installation required.*

---
&copy; 2026 Smart Campus Management System. All rights reserved.
