# 🗺️ Project Roadmap - Smart Campus Assistant

This document provides an overview of the development process for Smart Campus Assistant, a comprehensive campus management system built over 4 weeks.

---

## 🎯 Project Objectives

Develop a campus management system for IIIT Bangalore featuring:
- Smart timetable management based on course enrollments
- Course enrollment system with capacity management
- Event management and tracking
- Real-time notification system
- Role-based access control for students and administrators

---

## 📅 Development Timeline

**Duration:** November 11 - December 12, 2025 (4 weeks)  
**Team Size:** 6 members (Team BholeChature)  
**Methodology:** Agile development with daily coordination  
**Repository:** All code in `main` branch  
**Final Submission:** December 12, 2025

```
Week 1 (Nov 11-17): Requirements analysis, technology stack selection, initial setup
Week 2 (Nov 18-24): Backend development - entities, controllers, REST APIs
Week 3 (Nov 25-Dec 1): Frontend development and integration
Week 4 (Dec 2-9): Testing, bug fixes, documentation
Final Sprint (Dec 10-12): Quiz and messaging modules
```

---

## Week 1: Planning & Foundation (Nov 11-17, 2025)

Focused on requirements analysis, technology selection, and project setup.

### Activities

**Requirements Gathering (Nov 11-14)**
- Analyzed existing campus management system limitations
- Documented requirements in Software Requirements Specification (SRS)
- Selected Spring Boot + Thymeleaf over React-based architecture for faster development

**Technology Stack Finalization (Nov 15-16)**
- Backend: Spring Boot 3.4.12
- Frontend: Thymeleaf + Bootstrap 5
- Database: MySQL 8.0 on Aiven Cloud (SSL-secured)
- Security: Spring Security 6 with BCrypt

**Project Setup (Nov 17)**
- Module allocation among team members
- GitHub repository creation
- Postman workspace setup for API testing

### Deliverables
- Spring Boot project initialized with Maven
- Cloud database configured
- Development environment established
- Initial database schema designed

---

## Week 2: Backend Development (Nov 18-24, 2025)

Implemented core backend architecture including entities, repositories, services, and REST controllers.

### Key Implementations

**Authentication & Security (Nov 18-20)**
- Spring Security configuration with BCrypt password hashing
- User entity and CustomUserDetailsService
- Role-based access control (STUDENT, ADMIN)
- CSRF protection and session management

**Database Schema (Nov 20-21)**
- JPA entities: User, Course, CourseOffering, Faculty, StudentEnrollment, Timetable, TimetableEntry, Event, Notification
- Entity relationships with @OneToMany, @ManyToOne annotations
- Jakarta validation annotations

**Database Migrations (Nov 21-22)**
- Flyway migration scripts (V1-V4)
- Aiven Cloud MySQL integration with SSL
- Sample data population

**REST API Development (Nov 22-24)**
- Repository interfaces (JpaRepository)
- Service layer implementation
- 8 REST controllers with 54 endpoints
- Postman API testing

### Milestone
- Application successfully deployed on localhost:8080
- Cloud database connection established

---

## Week 3: Frontend Development & Integration (Nov 25 - Dec 1, 2025)

Developed user interface and integrated frontend with backend APIs.

### Major Features Implemented

**Timetable System (Nov 27-28)**
- Automatic timetable generation from course enrollments
- Weekly schedule view
- Conflict detection

**Dashboard Implementation**
- JdbcTemplate optimization for complex queries
- Real-time metrics: enrolled courses, today's classes, upcoming events
- Quick action widgets

**Events & Notifications**
- Event management with public/private visibility
- Automatic notification generation
- Unread notification tracking

**Template Development (Nov 29-30)**
- 22 Thymeleaf templates created:
  - Authentication pages (login, signup)
  - Student interface (dashboard, timetable, enrollment, events, notifications, profile, settings)
  - Admin interface (course, faculty, offering, timetable, event management)
  - Reusable fragments (header, navigation, footer, base layout)
- Bootstrap 5 integration for responsive design
- Custom CSS and JavaScript for interactivity

**Admin Panel (Dec 1)**
- Complete CRUD operations for all entities
- Form validation
- CSRF token implementation

### Milestone
- MVP functionality achieved (~90% completion)
- End-to-end user workflows operational

---

## Week 4: Testing & Documentation (Dec 2-9, 2025)

Comprehensive testing, bug fixes, and documentation preparation.

### Testing Phase (Dec 2-6)

**Integration Testing**
- Complete user flow validation:
  - Student workflow: Registration → Login → Enrollment → Timetable → Events
  - Admin workflow: Login → Course creation → Offering management → Faculty assignment → Timetable scheduling
- Enrollment capacity management verification
- Timetable conflict detection testing
- CSRF protection validation
- Cross-browser compatibility (Chrome, Firefox, Safari)

**Bug Fixes (Dec 7)**
- Theme toggle functionality
- Help & Support routing
- Schedule conflict detection edge cases
- Error message improvements

### Documentation (Dec 8-9)

**Submission Preparation**
- Clarified submission requirements with TA
- Updated README with individual contributions

**Documentation Created**
- README.md (project overview and contributions)
- API_REFERENCE.md (115 endpoints)
- ARCHITECTURE.md (system design)
- DATABASE_SCHEMA.md (schema and relationships)
- INSTALLATION.md (setup instructions)
- FEATURES.md (feature documentation)
- POSTMAN_TESTING.md (API testing guide)
- CONTRIBUTING.md (development workflow)
- TROUBLESHOOTING.md (common issues)
- DEVELOPER_SETUP.md (environment setup)
- ROADMAP.md (development timeline)

### Milestone
- MVP ready for submission with 95% feature completion

---

## Final Sprint: Additional Features (Dec 10-12, 2025)

Implemented quiz and messaging modules to enhance platform functionality.

### Quiz Review System (Dec 10-11)

**Admin Features**
- Quiz creation linked to course offerings
- Multiple questions with individual marks allocation
- Question-wise grading
- Comment/feedback system

**Student Features**
- Quiz listing for enrolled courses
- Grade viewing with detailed breakdown
- Question-wise feedback

**Implementation**
- V6 Flyway migration with 4 tables (quiz, quiz_question, quiz_attempt, quiz_answer)
- QuizService and QuizAttemptService
- AdminQuizController and StudentQuizController
- 12 Thymeleaf templates (10 admin, 2 student)

### Messaging Platform (Dec 11-12)

**Features**
- Direct messaging between students and administrators
- Broadcast messaging for campus-wide announcements
- Conversation threading
- Read/unread status tracking

**Implementation**
- Message entity (JPA-managed)
- MessageService with conversation logic
- AdminMessageController and StudentMessageController
- 9 Thymeleaf templates (5 admin, 4 student)

### Integration
- Both modules merged to main branch
- Included in final submission

---

## Key Learnings

### Technical Skills
- Spring Boot framework and MVC architecture
- Spring Security implementation
- JPA/Hibernate entity relationships
- RESTful API design and development
- Thymeleaf server-side rendering
- Bootstrap responsive design
- MySQL database design and optimization
- Flyway database migrations
- Cloud deployment (Aiven)

### Project Management
- Effective module ownership and task distribution
- Daily coordination and progress tracking
- Code review practices
- Version control with Git

### Challenges Addressed
- Spring Security configuration complexity
- SSL connection troubleshooting
- DTO pattern implementation for Thymeleaf
- Time management within 4-week constraint

---

## Project Statistics

### Final Metrics (December 12, 2025)

| Component | Count |
|-----------|-------|
| Controllers | 15 |
| Total Endpoints | 115 (63 REST APIs + 52 page routes) |
| Entities | 13 |
| Services | 17 |
| HTML Templates | 70+ |
| Database Tables | 13 (12 Flyway + 1 JPA) |
| Flyway Migrations | 6 |
| Documentation Files | 11 |

### Features Implemented

**Core Modules**
- Authentication & Authorization (Spring Security, BCrypt, CSRF)
- Student Dashboard (real-time metrics, JdbcTemplate optimization)
- Course Management (full CRUD operations)
- Smart Timetable (enrollment-based generation)
- Event Management (public/private visibility)
- Notification System (bulk generation, read tracking)
- Faculty Directory (department filtering)
- Enrollment System (capacity management)
- Admin Panel (complete CRUD interface)

**Additional Modules (Dec 10-12)**
- Quiz Review System (creation, grading, feedback)
- Messaging Platform (direct messaging, broadcasts)

### Team Contributions

All team members contributed equally over the 4-week period, with specific module ownership:

- **Jashandeep Singh Bedi (IMT2024022):** Authentication, security, enrollment & faculty APIs, database architecture
- **Kanav Kumar (BT2024021):** Enrollment workflow, notifications, events
- **Pulkit Pandey (BT2024060):** Course APIs, student portal, entity models
- **Soham Banerjee (BT2024128):** Admin panel, faculty management, dashboard, deployment
- **Harsh Kumar (BT2024008):** Frontend development, UI/UX, templates
- **Dayal Gupta (BT2024167):** Quiz system, messaging platform

---

## Project Deliverables

**Project Name:** Smart Campus Assistant  
**Team:** BholeChature  
**Institution:** IIIT Bangalore  
**Development Period:** November 11 - December 12, 2025  
**Submission Date:** December 12, 2025

### Codebase (Main Branch)
- 15 controllers with 115 endpoints
- 13 JPA entities
- 17 service classes
- 70+ responsive Thymeleaf templates
- 6 Flyway migrations + 1 JPA-managed table
- Spring Security implementation
- Deployed on Aiven Cloud MySQL

### Documentation
- README.md - Project overview and team contributions
- API_REFERENCE.md - Complete endpoint documentation
- ARCHITECTURE.md - System design and architecture
- DATABASE_SCHEMA.md - Database structure and relationships
- FEATURES.md - Feature specifications
- INSTALLATION.md - Setup and deployment guide
- POSTMAN_TESTING.md - API testing procedures
- CONTRIBUTING.md - Development workflow
- TROUBLESHOOTING.md - Common issues and solutions
- DEVELOPER_SETUP.md - Environment configuration
- ROADMAP.md - Development timeline

### Technical Highlights
- MVC architecture with clear separation of concerns
- Three-tier design (Controller → Service → Repository)
- Security: BCrypt hashing, CSRF protection, role-based access, SSL
- Performance: JdbcTemplate for complex queries, lazy loading, database indexing
- Code quality: Consistent conventions, comprehensive comments, meaningful commits

---

**Repository:** https://github.com/Jdsb06/SmartCampus  
**Branch:** `main`  
**Team:** BholeChature  
**Submission Date:** December 12, 2025

---

<div align="center">

**Built by Team BholeChature**  
**IIIT Bangalore • December 2025**

</div>
