# 🐛 Troubleshooting Guide - Smart Campus Assistant

Common issues and their solutions for Smart Campus Assistant.

---

## 🚀 Application Startup Issues

### Issue: Application Fails to Start

**Symptoms**:
- Application crashes on startup
- "Application failed to start" error
- Port binding errors

**Solutions**:

#### 1. Port Already in Use

```bash
# Error message:
# "Port 8080 was already in use"

# Solution (Linux/macOS):
lsof -i :8080
kill -9 <PID>

# Solution (Windows):
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Or change port in application.properties:
server.port=9090
```

#### 2. Database Connection Failed

```bash
# Error message:
# "Communications link failure"
# "Access denied for user 'scauser'@'localhost'"

# Solutions:
# 1. Check MySQL is running
sudo systemctl status mysql  # Linux
brew services list  # macOS

# 2. Verify credentials in application.properties
spring.datasource.username=scauser
spring.datasource.password=scapassword

# 3. Test connection manually
mysql -u scauser -p -h localhost

# 4. Check firewall (allow port 3306)
sudo ufw allow 3306  # Linux
```

#### 3. JAVA_HOME Not Set

```bash
# Error message:
# "JAVA_HOME environment variable is not set"

# Solution (Linux/macOS):
export JAVA_HOME=/usr/lib/jvm/java-24-openjdk
echo 'export JAVA_HOME=/usr/lib/jvm/java-24-openjdk' >> ~/.bashrc
source ~/.bashrc

# Solution (Windows):
# Set JAVA_HOME in System Environment Variables
# JAVA_HOME = C:\Program Files\Java\jdk-24
```

---

## 🔐 Authentication & Authorization Issues

### Issue: Login Redirect Loop

**Symptoms**:
- After successful login, redirects back to login page
- No errors in console
- Session not created

**Solutions**:

#### 1. Check User Exists in Database

```sql
-- Connect to MySQL
mysql -u scauser -p smartcampus

-- Check user exists
SELECT * FROM users WHERE username = 'admin';

-- If not exists, create user:
INSERT INTO users (username, password, role, full_name) 
VALUES ('admin', '$2a$10$...', 'ADMIN', 'Admin User');
```

#### 2. Verify Password Encoding

```java
// In AuthController or test class
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("admin123");
System.out.println(hashedPassword);

// Update database with correct hash
UPDATE users SET password = '<hashed_password>' WHERE username = 'admin';
```

#### 3. Check Security Configuration

```java
// SecurityConfig.java should have:
.formLogin()
    .loginPage("/login")
    .defaultSuccessUrl("/dashboard", true)  // Force redirect
    .failureUrl("/login?error")
    .permitAll()
```

---

### Issue: 403 Forbidden Error

**Symptoms**:
- "Access Denied" page
- "Forbidden" error on API calls
- User is logged in but cannot access certain pages

**Solutions**:

#### 1. Check Role-Based Access

```java
// Ensure user has correct role
SELECT role FROM users WHERE username = 'IMT2022001';

// Should return 'STUDENT' or 'ADMIN' (not 'ROLE_STUDENT')

// If wrong, update:
UPDATE users SET role = 'STUDENT' WHERE username = 'IMT2022001';
```

#### 2. Verify Endpoint Permissions

```java
// SecurityConfig.java
.requestMatchers("/admin/**").hasRole("ADMIN")  // No 'ROLE_' prefix
.requestMatchers("/api/courses/**").hasRole("ADMIN")
.requestMatchers("/pages/**").hasAnyRole("STUDENT", "ADMIN")
```

#### 3. Check CSRF Token

```javascript
// Ensure CSRF token is included in AJAX requests
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

fetch(url, {
    headers: {
        [csrfHeader]: csrfToken,
        'Content-Type': 'application/json'
    }
});
```

---

## 🗄️ Database Issues

### Issue: Flyway Migration Failed

**Symptoms**:
- "Migration checksum mismatch"
- "Validate failed: Migration missing"
- Application fails to start due to Flyway errors

**Solutions**:

#### 1. Repair Flyway Schema

```bash
# Using Maven
./mvnw flyway:repair

# Or connect to database and reset:
mysql -u scauser -p smartcampus
DROP TABLE flyway_schema_history;
```

#### 2. Clean and Rebuild (CAUTION: Deletes Data)

```bash
# Drop database
mysql -u root -p
DROP DATABASE smartcampus;
CREATE DATABASE smartcampus;
EXIT;

# Restart application (Flyway will recreate tables)
./mvnw spring-boot:run
```

#### 3. Disable Flyway Temporarily

```properties
# application.properties
spring.flyway.enabled=false
```

---

### Issue: Table Already Exists Error

**Symptoms**:
- "Table 'users' already exists"
- Hibernate tries to create tables that already exist

**Solutions**:

#### 1. Change Hibernate DDL Strategy

```properties
# application.properties
# Change from 'create' or 'create-drop' to:
spring.jpa.hibernate.ddl-auto=update  # For development
# OR
spring.jpa.hibernate.ddl-auto=validate  # For production
```

#### 2. Use Flyway for Schema Management

```properties
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

---

## 🌐 Frontend / Template Issues

### Issue: Whitelabel Error Page

**Symptoms**:
- White page with "Whitelabel Error Page"
- 404 Not Found
- "There was an unexpected error (type=Not Found, status=404)"

**Solutions**:

#### 1. Check Template Location

```bash
# Templates should be in:
src/main/resources/templates/

# Verify file exists:
ls src/main/resources/templates/login.html
```

#### 2. Verify Controller Mapping

```java
@GetMapping("/dashboard")
public String dashboard(Model model, Authentication authentication) {
    return "pages/dashboard";  // Maps to templates/pages/dashboard.html
}
```

#### 3. Check Thymeleaf Configuration

```properties
# application.properties
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.cache=false  # For development
```

---

### Issue: Template Parsing Error

**Symptoms**:
- "Error resolving template"
- "Could not resolve fragment"
- Thymeleaf syntax errors

**Solutions**:

#### 1. Fix Fragment Syntax

```html
<!-- WRONG -->
<html th:replace="~{fragments/base :: layout}">

<!-- CORRECT -->
<html th:replace="~{fragments/base :: layout(~{::content})}">
<div th:fragment="content">
    <!-- Page content -->
</div>
</html>
```

#### 2. Check Fragment Definition

```html
<!-- fragments/base.html -->
<html th:fragment="layout(content)">
<head>
    <!-- Head content -->
</head>
<body>
    <div th:replace="${content}"></div>
</body>
</html>
```

---

### Issue: Static Resources Not Loading

**Symptoms**:
- CSS/JS files return 404
- Bootstrap styles not applied
- Images not displaying

**Solutions**:

#### 1. Verify Static Resource Location

```bash
# Static files should be in:
src/main/resources/static/

# Structure:
static/
  css/
    main.css
  js/
    main.js
  images/
```

#### 2. Reference Correctly in Templates

```html
<!-- CORRECT -->
<link rel="stylesheet" th:href="@{/css/main.css}">
<script th:src="@{/js/main.js}"></script>

<!-- WRONG -->
<link rel="stylesheet" href="../static/css/main.css">
```

#### 3. Check Security Configuration

```java
// SecurityConfig.java
.requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
```

---

## 📡 API / AJAX Issues

### Issue: CORS Error

**Symptoms**:
- "CORS policy: No 'Access-Control-Allow-Origin' header"
- Cross-origin requests blocked

**Solutions**:

#### 1. Add CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:8080")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true);
    }
}
```

---

### Issue: CSRF Token Mismatch

**Symptoms**:
- "Invalid CSRF Token"
- 403 Forbidden on POST/PUT/DELETE requests

**Solutions**:

#### 1. Include CSRF Token in Forms

```html
<!-- Thymeleaf automatically includes CSRF in forms -->
<form th:action="@{/api/courses}" method="post">
    <!-- Form fields -->
</form>
```

#### 2. Include CSRF in AJAX Requests

```javascript
// Get token from meta tags
const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

// Include in fetch
fetch('/api/enrollments', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        [csrfHeader]: csrfToken
    },
    body: JSON.stringify(data)
});
```

#### 3. Add Meta Tags to Templates

```html
<head>
    <meta name="_csrf" th:content="${_csrf.token}"/>
    <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
</head>
```

---

## 🔄 Data Display Issues

### Issue: Nested DTO Properties Show as "N/A"

**Symptoms**:
- Course code displays "N/A" instead of actual code
- Faculty name shows "TBA"
- Nested object properties not accessible

**Solutions**:

#### 1. Use Correct Property Path

```html
<!-- WRONG -->
<td th:text="${offering.courseCode}">N/A</td>

<!-- CORRECT -->
<td th:text="${offering.course?.code}">N/A</td>

<!-- JavaScript (extract from nested DTO) -->
const courseCode = offering.course?.code || 'N/A';
```

#### 2. Verify DTO Structure

```java
// CourseOfferingResponseDTO should have:
@Data
public class CourseOfferingResponseDTO {
    private Long id;
    private CourseResponseDTO course;  // Nested
    private FacultyResponseDTO faculty;  // Nested
}
```

---

### Issue: Timetable Shows Empty

**Symptoms**:
- Timetable grid displays but no classes shown
- Student has enrollments but timetable is blank

**Solutions**:

#### 1. Check Timetable Entries Exist

```sql
-- Verify timetable entries linked to student's enrollments
SELECT te.*, co.id as offering_id, se.user_id
FROM timetable_entries te
JOIN course_offerings co ON te.offering_id = co.id
JOIN student_enrollments se ON se.offering_id = co.id
WHERE se.user_id = 1;
```

#### 2. Verify API Returns Data

```bash
# Test API endpoint
curl http://localhost:8080/api/timetable/student/1 \
  -H "Cookie: JSESSIONID=..."

# Should return array of timetable entries
```

---

## ⚙️ Performance Issues

### Issue: Slow Dashboard Load

**Symptoms**:
- Dashboard takes 5+ seconds to load
- Multiple database queries in logs

**Solutions**:

#### 1. Use JdbcTemplate for Dashboard

```java
// Instead of multiple JPA queries, use single JdbcTemplate query
@Service
public class DashboardServiceImpl implements DashboardService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public DashboardResponse getDashboardData(Long userId) {
        String sql = "SELECT " +
            "(SELECT COUNT(*) FROM student_enrollments WHERE user_id = ?) as enrolledCourses, " +
            "(SELECT COUNT(*) FROM timetable_entries WHERE ...) as todayClasses";
        
        return jdbcTemplate.queryForObject(sql, ...);
    }
}
```

#### 2. Add Database Indexes

```sql
CREATE INDEX idx_enrollments_user ON student_enrollments(user_id);
CREATE INDEX idx_timetable_offering ON timetable_entries(offering_id);
```

---

## 🔧 Build & Deployment Issues

### Issue: Maven Build Fails

**Symptoms**:
- "BUILD FAILURE"
- Dependency download errors
- Compilation errors

**Solutions**:

#### 1. Clear Maven Cache

```bash
# Clear local repository
rm -rf ~/.m2/repository

# Rebuild
./mvnw clean install
```

#### 2. Skip Tests

```bash
./mvnw clean install -DskipTests
```

#### 3. Update Dependencies

```bash
./mvnw clean install -U  # Force update snapshots
```

---

## 📞 Still Having Issues?

If your issue is not listed here:

1. **Check Logs**: Look for stack traces in console or log files
2. **Search Issues**: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues)
3. **Ask for Help**: Create new issue with:
   - Clear description
   - Steps to reproduce
   - Error logs
   - Environment details (OS, Java version, etc.)

---

**Last Updated**: February 2025  
**Maintained by**: Team BholeChature
