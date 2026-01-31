# 🏗️ System Architecture - Smart Campus Assistant

This document provides a comprehensive overview of the Smart Campus Assistant's architecture, technology stack, design patterns, and system design decisions.

**Last Updated**: December 12, 2025  
**Architecture Version**: 2.1  
**Project Status**: Production MVP (100% Complete with Advanced Features)

---

## 📐 System Architecture Overview

Smart Campus Assistant follows a **3-tier MVC (Model-View-Controller)** architecture pattern with clear separation of concerns.

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  (Thymeleaf Templates + Bootstrap 5 + Vanilla JavaScript)   │
└──────────────────────┬──────────────────────────────────────┘
                       │ HTTP/HTTPS
┌──────────────────────┴──────────────────────────────────────┐
│                      Controller Layer                        │
│  (Spring MVC Controllers + REST Controllers + Security)     │
└──────────────────────┬──────────────────────────────────────┘
                       │ Service Calls
┌──────────────────────┴──────────────────────────────────────┐
│                       Service Layer                          │
│         (Business Logic + Data Transformation)              │
└──────────────────────┬──────────────────────────────────────┘
                       │ Repository Calls
┌──────────────────────┴──────────────────────────────────────┐
│                     Repository Layer                         │
│     (JPA Repositories + JdbcTemplate + Custom Queries)      │
└──────────────────────┬──────────────────────────────────────┘
                       │ JDBC/SQL
┌──────────────────────┴──────────────────────────────────────┐
│                      Database Layer                          │
│        (MySQL 8.0 on Aiven Cloud with SSL/TLS)             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack (Detailed)

### Backend Technologies

#### Core Framework
- **Java**: 24/25 (Latest LTS features)
- **Spring Boot**: 3.4.12
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
  - Spring JDBC
  - Spring Validation

#### Security
- **Spring Security**: 6.x
  - Form-based authentication
  - BCrypt password encoder
  - CSRF protection
  - Role-based authorization (`@PreAuthorize`)
  - Session management

#### Database & ORM
- **MySQL**: 8.0.35 (Aiven Cloud)
- **Hibernate**: 6.6.36 (JPA implementation)
- **Flyway**: Database migrations
- **JDBC Template**: Performance-critical queries (Dashboard)

#### Build & Dependency Management
- **Maven**: 3.8+
- **Lombok**: Reduces boilerplate code

### Frontend Technologies

#### Template Engine
- **Thymeleaf**: 3.1.3
  - Server-side rendering
  - Layout dialect for reusable fragments
  - Spring Security integration
  - Natural templates

#### UI Framework
- **Bootstrap**: 5.3.0
  - Responsive grid system
  - Component library
  - Utility classes
  - Icons

#### JavaScript
- **Vanilla JavaScript** (ES6+)
  - Fetch API for AJAX calls
  - DOM manipulation
  - Event handling
  - No heavy framework dependencies

### DevOps & Tools

#### Database Hosting
- **Aiven Cloud MySQL**
  - Managed database service
  - Automatic backups
  - SSL/TLS encryption
  - High availability

#### Version Control
- **Git** + **GitHub**
  - Feature branch workflow
  - Pull request reviews
  - Issue tracking

---

## 🏛️ Architectural Layers

### 1. Presentation Layer

**Location**: `src/main/resources/templates/`

**Responsibilities**:
- Render HTML pages using Thymeleaf
- Display dynamic data from controllers
- Handle user inputs via forms
- Client-side validation
- AJAX calls to REST endpoints

**Key Components**:
- `fragments/base.html` - Master layout template
- `fragments/header.html` - Navigation header
- `pages/*` - Student-facing pages
- `admin/*` - Admin panel pages
- `login.html`, `signup.html` - Authentication pages

**Design Pattern**: **Fragment-based Layout System**
```html
<!-- All pages extend base layout -->
<html th:replace="~{fragments/base :: layout(~{::content})}">
<div th:fragment="content">
    <!-- Page-specific content -->
</div>
</html>
```

---

### 2. Controller Layer

**Location**: `src/main/java/com/sca/smartcampusbackend/controller/`

**Responsibilities**:
- Handle HTTP requests
- Route requests to appropriate services
- Transform service responses to DTOs
- Apply security annotations
- Handle exceptions

**Controllers**:

| Controller | Type | Endpoints | Purpose |
|------------|------|-----------|---------|
| `AuthController` | View | `/login`, `/register` | Authentication |
| `PageController` | View | `/dashboard`, `/timetable`, etc. | Page routing |
| `DashboardController` | REST | `/api/dashboard/*` | Dashboard data |
| `TimetableController` | REST | `/api/timetable/*` | Timetable CRUD |
| `EnrollmentController` | REST | `/api/enrollments/*` | Enrollment CRUD |
| `EventController` | REST | `/api/events/*` | Event CRUD |
| `CourseController` | REST | `/api/courses/*` | Course CRUD |
| `FacultyController` | REST | `/api/faculty/*` | Faculty CRUD |
| `CourseOfferingController` | REST | `/api/offerings/*` | Offerings CRUD |
| `TimetableEntryController` | REST | `/api/timetable/*` | Timetable CRUD |
| `NotificationController` | REST | `/api/notifications/*` | Notifications |
| `AdminQuizController` | View | `/admin/quizzes/*` | Quiz management |
| `StudentQuizController` | View | `/student/grades/*` | Student grades |
| `AdminMessageController` | View | `/admin/messages/*` | Admin messaging |
| `StudentMessageController` | View | `/student/messages/*` | Student messaging |

**Design Pattern**: **RESTful API Design**
- GET for read operations
- POST for create operations
- PUT for full updates
- DELETE for deletions
- Proper HTTP status codes (200, 201, 204, 400, 404, 500)

---

### 3. Service Layer

**Location**: `src/main/java/com/sca/smartcampusbackend/service/` & `service/impl/`

**Responsibilities**:
- Implement business logic
- Data validation
- Entity to DTO transformation
- Transaction management
- Exception handling

**Services**:
- `DashboardService` - Complex queries using JdbcTemplate
- `TimetableService` - Schedule management logic
- `EnrollmentService` - Enrollment capacity validation
- `EventService` - Event visibility logic
- `CourseService` - Course CRUD operations
- `FacultyService` - Faculty management
- `QuizService` - Quiz management and retrieval
- `QuizQuestionService` - Question management
- `QuizAttemptService` - Student attempt tracking
- `QuizGradingService` - Faculty grading operations
- `MessageService` - Messaging and broadcast logic
- `CourseOfferingService` - Offering creation and linking
- `NotificationService` - Notification delivery

**Design Pattern**: **Service Interface Pattern**
```java
public interface DashboardService {
    DashboardResponse getDashboardData(Long userId);
}

@Service
public class DashboardServiceImpl implements DashboardService {
    @Override
    public DashboardResponse getDashboardData(Long userId) {
        // Implementation using JdbcTemplate
    }
}
```

---

### 4. Repository Layer

**Location**: `src/main/java/com/sca/smartcampusbackend/repository/`

**Responsibilities**:
- Database access
- CRUD operations
- Custom queries
- Data persistence

**Repositories**:
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {
    @Query("SELECT te FROM TimetableEntry te JOIN te.courseOffering co JOIN StudentEnrollment se WHERE se.user.id = :userId")
    List<TimetableEntry> findByStudentId(@Param("userId") Long userId);
}
```

**Design Pattern**: **Spring Data JPA Repository Pattern**
- Automatic CRUD implementation
- Custom query methods via `@Query`
- Derived query methods (e.g., `findByUsername`)

---

### 5. Data Transfer Objects (DTOs)

**Location**: `src/main/java/com/sca/smartcampusbackend/dto/`

**Purpose**: Decouple internal entities from API responses

**Key DTOs**:
- `CourseOfferingResponseDTO` - Nested DTO with `CourseResponseDTO` and `FacultyResponseDTO`
- `EnrollmentResponseDTO` - Enrollment with course details
- `DashboardResponse` - Aggregated dashboard metrics
- `EventResponseDTO` - Event with creator info

**Design Pattern**: **Nested DTO Pattern**
```java
public class CourseOfferingResponseDTO {
    private Long id;
    private CourseResponseDTO course;  // Nested
    private FacultyResponseDTO faculty; // Nested
    private Integer semester;
    private Integer enrolledCount;
    private Integer maxCapacity;
}
```

---

## 🔐 Security Architecture

### Authentication Flow

```
1. User submits login form → POST /login
2. Spring Security intercepts request
3. UserDetailsService loads user from database
4. PasswordEncoder verifies password (BCrypt)
5. On success: Create authentication token + session
6. Redirect to /dashboard
7. On failure: Redirect to /login?error
```

### Authorization Model

**Roles**: `STUDENT`, `ADMIN`

**Access Control**:
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public void createCourse(CourseRequestDTO dto) { }

// URL-based security (SecurityConfig)
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/courses/**").hasRole("ADMIN")
    .requestMatchers("/pages/**").hasAnyRole("STUDENT", "ADMIN")
    .anyRequest().authenticated()
);
```

### CSRF Protection

**Implementation**:
- CSRF tokens generated for all state-changing operations
- Thymeleaf automatically includes CSRF in forms
- JavaScript AJAX calls manually include CSRF token

```javascript
// fetchWithCSRF utility function
function fetchWithCSRF(url, options = {}) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    
    options.headers = {
        ...options.headers,
        [csrfHeader]: csrfToken,
        'Content-Type': 'application/json'
    };
    
    return fetch(url, options);
}
```

---

## 🗄️ Database Architecture

### Entity Relationships

```
┌─────────┐
│  users  │
└────┬────┘
     │ 1
     ├────────────┐
     │ *          │ *
┌────┴────────┐  ┌┴──────────────┐
│ enrollments │  │ notifications │
└─────┬───────┘  └───────────────┘
      │ *
      │ 1
┌─────┴──────────┐
│ course_offerings│
└────┬──┬────────┘
     │* │ *
  1  │  │  1
┌────┴──┴┐  ┌────────┐
│ courses│  │ faculty│
└────────┘  └────────┘
     │
     │ 1
     │ *
┌────┴───────────┐
│ timetable_entries│
└─────────────────┘
```

### Data Access Strategies

**JPA (Default)**:
- Standard CRUD operations
- Entity relationships
- Lazy/Eager loading
- Caching

**JdbcTemplate (Performance-Critical)**:
- Dashboard queries (joins 5+ tables)
- Complex aggregations
- Custom result set mapping

Example:
```java
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public DashboardResponse getDashboardData(Long userId) {
        String sql = "SELECT " +
            "(SELECT COUNT(*) FROM student_enrollments WHERE user_id = ?) as enrolledCourses, " +
            "(SELECT COUNT(*) FROM timetable_entries te JOIN course_offerings co ON te.offering_id = co.id JOIN student_enrollments se ON se.offering_id = co.id WHERE se.user_id = ? AND te.day_of_week = ?) as todayClasses";
        
        return jdbcTemplate.queryForObject(sql, 
            (rs, rowNum) -> new DashboardResponse(
                rs.getInt("enrolledCourses"),
                rs.getInt("todayClasses")
            ), 
            userId, userId, LocalDate.now().getDayOfWeek().toString()
        );
    }
}
```

---

## 🎨 Design Patterns Used

### 1. **MVC Pattern**
- Separation of presentation, business logic, and data

### 2. **Repository Pattern**
- Abstract data access logic
- Testable and swappable implementations

### 3. **Service Layer Pattern**
- Centralize business logic
- Transaction boundaries

### 4. **DTO Pattern**
- API response/request objects
- Prevent entity exposure

### 5. **Builder Pattern** (via Lombok)
```java
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    private Long id;
    private String code;
    private String title;
}
```

### 6. **Singleton Pattern**
- Spring beans are singletons by default

### 7. **Factory Pattern**
- `JpaRepository` creates repository implementations

---

## 📊 Performance Optimizations

### 1. **Database Indexing**
```sql
-- Indexes on foreign keys
CREATE INDEX idx_enrollments_user ON student_enrollments(user_id);
CREATE INDEX idx_enrollments_offering ON student_enrollments(offering_id);
CREATE INDEX idx_timetable_offering ON timetable_entries(offering_id);
```

### 2. **Query Optimization**
- Use JdbcTemplate for complex aggregations
- Lazy loading for collections
- `@EntityGraph` to prevent N+1 queries

### 3. **Caching** (Future Enhancement)
- Spring Cache abstraction
- Redis integration
- Cache frequently accessed data (courses, faculty)

### 4. **Connection Pooling**
- HikariCP (default in Spring Boot)
- Configured pool size based on load

---

## 🔄 Data Flow Examples

### Example 1: Student Enrolls in Course

```
1. Student clicks "Enroll" button
2. JavaScript fetchWithCSRF sends POST /api/enrollments
3. EnrollmentController receives request
4. Security filter validates CSRF + authentication
5. Controller calls EnrollmentService.enroll()
6. Service checks:
   - Course offering exists
   - Student not already enrolled
   - Capacity available
7. Service creates StudentEnrollment entity
8. Repository saves to database
9. Service increments enrolledCount on CourseOffering
10. Controller returns 201 Created
11. JavaScript shows success toast
12. Page updates enrollment list
```

### Example 2: Dashboard Load

```
1. User navigates to /dashboard
2. PageController intercepts request
3. Security filter validates session
4. Controller gets userId from authentication
5. Controller calls DashboardService.getDashboardData(userId)
6. Service executes JdbcTemplate query:
   - Joins users, enrollments, timetable_entries, events, notifications
   - Aggregates counts
   - Fetches today's schedule
   - Fetches upcoming events
7. Service returns DashboardResponse DTO
8. Controller adds DTO to model
9. Thymeleaf renders dashboard.html with data
10. Browser displays dashboard
```

---

## 🧪 Testing Strategy (Future Enhancement)

### Unit Tests
- Service layer business logic
- DTO transformations
- Validation rules

### Integration Tests
- Controller endpoints
- Database queries
- Security configurations

### End-to-End Tests
- User workflows (login, enroll, create event)
- Selenium/Playwright for UI testing

---

## 📈 Scalability Considerations

### Current Architecture Limitations
- Monolithic application (all modules in one app)
- Single database instance
- No load balancing

### Future Enhancements for Scale
1. **Microservices Architecture**
   - Auth Service
   - Course Service
   - Event Service
   - Notification Service
2. **Database Sharding**
   - Partition by university/campus
3. **Caching Layer**
   - Redis for session storage
   - Cache frequently accessed data
4. **Load Balancing**
   - Multiple app instances
   - Nginx/HAProxy
5. **CDN for Static Assets**
   - Bootstrap, CSS, JS files
6. **Message Queue**
   - RabbitMQ/Kafka for async tasks
   - Email notifications, bulk operations

---

## 🛡️ Security Best Practices

✅ **Implemented**:
- BCrypt password hashing
- CSRF protection
- SQL injection prevention (JPA/JdbcTemplate)
- XSS prevention (Thymeleaf escaping)
- HTTPS enforcement (Aiven SSL)
- Session fixation protection
- Role-based access control

🔄 **Planned**:
- Rate limiting (Spring Cloud Gateway)
- Input validation (JSR-303)
- CORS configuration
- Security headers (Helmet.js equivalent)
- Audit logging

---

## 📞 Architecture Questions?

For architecture-related questions or suggestions:
- Open an issue with label `architecture`
- Contact: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues)

---

**Last Updated**: February 2025  
**Maintained by**: Team BholeChature
