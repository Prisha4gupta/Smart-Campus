# 🚀 Developer Setup Guide - Smart Campus Assistant

Complete guide for developers to set up the Smart Campus Assistant development environment.

---

## 🎯 Overview

This guide is for developers who want to:
- Contribute to the project
- Set up a local development environment
- Understand the development workflow
- Debug and test features

For basic installation, see [INSTALLATION.md](INSTALLATION.md).

---

## 📋 Prerequisites

### Required Tools

| Tool | Version | Purpose | Installation |
|------|---------|---------|--------------|
| **Java JDK** | 24/25 | Backend development | [OpenJDK](https://openjdk.org/) |
| **Maven** | 3.8+ | Build tool | [Maven](https://maven.apache.org/) |
| **Git** | Latest | Version control | [Git](https://git-scm.com/) |
| **IDE** | - | Code editor | [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended) |
| **MySQL** | 8.0+ | Database | [MySQL](https://dev.mysql.com/) or Aiven Cloud |

### Recommended Tools

| Tool | Purpose |
|------|---------|
| **Postman** | API testing |
| **MySQL Workbench** | Database management |
| **Git GUI** | GitHub Desktop, SourceTree |
| **Docker** | Containerization (future) |

---

## 🛠️ IDE Setup

### IntelliJ IDEA (Recommended)

#### 1. Install IntelliJ IDEA

Download: [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/) (free)

#### 2. Install Required Plugins

Go to: **File → Settings → Plugins**

Install:
- **Lombok** (required)
- **Spring Boot Assistant**
- **Database Navigator**
- **Rainbow Brackets**
- **Key Promoter X**

#### 3. Import Project

```bash
# Clone repository
git clone https://github.com/Jdsb06/SmartCampus.git
cd SmartCampus/smart-campus-backend

# Open in IntelliJ
# File → Open → Select 'smart-campus-backend' folder
# Wait for Maven import to complete
```

#### 4. Configure Java SDK

1. **File → Project Structure → Project**
2. Set **Project SDK**: Java 24 or 25
3. Set **Language Level**: 24 or 25

#### 5. Enable Annotation Processing (for Lombok)

1. **File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors**
2. Check **Enable annotation processing**

#### 6. Configure Code Style

1. **File → Settings → Editor → Code Style → Java**
2. Set **Tab size**: 4
3. Set **Indent**: 4
4. Set **Continuation indent**: 8
5. Enable **Use tab character**: No (use spaces)

---

### VS Code Setup (Alternative)

#### 1. Install VS Code

Download: [VS Code](https://code.visualstudio.com/)

#### 2. Install Extensions

- **Extension Pack for Java** (Microsoft)
- **Spring Boot Extension Pack** (VMware)
- **Lombok Annotations Support**
- **MySQL** (by Jun Han)

#### 3. Open Project

```bash
code smart-campus-backend/
```

---

## 🗄️ Database Setup

### Option 1: Local MySQL

#### 1. Install MySQL

```bash
# macOS
brew install mysql
brew services start mysql

# Ubuntu/Debian
sudo apt install mysql-server
sudo systemctl start mysql

# Windows
# Download and run MySQL Installer
```

#### 2. Create Database

```sql
mysql -u root -p

CREATE DATABASE smartcampus;
CREATE USER 'scauser'@'localhost' IDENTIFIED BY 'scapassword';
GRANT ALL PRIVILEGES ON smartcampus.* TO 'scauser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### 3. Configure application.properties

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/smartcampus?useSSL=false
spring.datasource.username=scauser
spring.datasource.password=scapassword
```

---

### Option 2: Aiven Cloud MySQL

#### 1. Sign up for Aiven

1. Go to [Aiven](https://aiven.io/)
2. Create free account
3. Create MySQL 8.0 service

#### 2. Get Connection Details

1. Copy **Service URI**
2. Note **Username** and **Password**
3. Download **CA Certificate** (for SSL)

#### 3. Configure application.properties

```properties
spring.datasource.url=jdbc:mysql://mysql-xxxxx.aivencloud.com:12345/defaultdb?sslMode=REQUIRED
spring.datasource.username=avnadmin
spring.datasource.password=YOUR_AIVEN_PASSWORD
```

---

## 🔧 Environment Variables (Recommended)

**Why?** Don't commit passwords to Git.

### Create .env file

```bash
# Create .env in project root (add to .gitignore)
DB_URL=jdbc:mysql://localhost:3306/smartcampus
DB_USERNAME=scauser
DB_PASSWORD=scapassword
```

### Update application.properties

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Load in IntelliJ

1. Install **EnvFile** plugin
2. Run → Edit Configurations
3. Select **SmartCampusBackendApplication**
4. Add **EnvFile** tab → Select `.env` file

---

## 🏃 Running the Application

### Method 1: IntelliJ Run Configuration

1. Open `SmartCampusBackendApplication.java`
2. Right-click → **Run 'SmartCampusBackendApplication'**
3. Or click green play button in top-right

### Method 2: Maven Command Line

```bash
# From project root
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Method 3: Build and Run JAR

```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/smart-campus-backend-0.0.1-SNAPSHOT.jar
```

---

## 🔍 Debugging

### IntelliJ Debugger

1. Set breakpoints (click left gutter in code editor)
2. Run → Debug 'SmartCampusBackendApplication'
3. Use Debug toolbar:
   - **Step Over** (F8)
   - **Step Into** (F7)
   - **Resume** (F9)

### Remote Debugging

```bash
# Run with debug enabled
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar app.jar

# In IntelliJ:
# Run → Edit Configurations → + → Remote JVM Debug
# Set port: 5005
# Start debugger
```

---

## 🧪 Testing

### Run All Tests

```bash
./mvnw test
```

### Run Specific Test Class

```bash
./mvnw test -Dtest=EnrollmentServiceTest
```

### Run in IntelliJ

1. Right-click on test class or method
2. **Run 'testMethodName()'**

### Test Coverage (Future)

```bash
./mvnw test jacoco:report
# Report in: target/site/jacoco/index.html
```

---

## 🔄 Database Migrations

### Flyway Commands

```bash
# Check migration status
./mvnw flyway:info

# Run migrations
./mvnw flyway:migrate

# Repair failed migration
./mvnw flyway:repair

# Clean database (CAUTION: deletes all data)
./mvnw flyway:clean
```

### Creating New Migration

1. Create file: `src/main/resources/db/migration/V{version}__{description}.sql`
   - Example: `V4__add_groups_feature.sql`
2. Write SQL statements
3. Restart application (Flyway auto-runs migrations)

---

## 📦 Managing Dependencies

### Add New Dependency

Edit `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

Reload Maven:
- IntelliJ: **Maven → Reload Project**
- Command line: `./mvnw clean install`

### Update Dependency Version

```xml
<properties>
    <spring-boot.version>3.4.12</spring-boot.version>
</properties>
```

### Check for Updates

```bash
./mvnw versions:display-dependency-updates
```

---

## 🌳 Git Workflow

### 1. Fork & Clone

```bash
# Fork on GitHub (click Fork button)

# Clone your fork
git clone https://github.com/YOUR_USERNAME/SmartCampus.git
cd SmartCampus/smart-campus-backend

# Add upstream
git remote add upstream https://github.com/Jdsb06/SmartCampus.git
```

### 2. Create Feature Branch

```bash
git checkout -b feature/notification-panel
```

### 3. Make Changes & Commit

```bash
git add .
git commit -m "feat(notifications): add notification panel UI"
```

### 4. Keep Branch Updated

```bash
# Fetch latest from upstream
git fetch upstream

# Rebase on upstream/main
git rebase upstream/main

# Resolve conflicts if any
```

### 5. Push & Create PR

```bash
git push origin feature/notification-panel

# Go to GitHub and create Pull Request
```

---

## 🛠️ Useful Commands

### Maven

```bash
# Clean build
./mvnw clean install

# Skip tests
./mvnw clean install -DskipTests

# Run specific goal
./mvnw spring-boot:run
./mvnw flyway:migrate

# Dependency tree
./mvnw dependency:tree
```

### Git

```bash
# Check status
git status

# View changes
git diff

# Stash changes
git stash
git stash pop

# View commit history
git log --oneline --graph

# Undo last commit (keep changes)
git reset --soft HEAD~1
```

---

## 🔧 Common Development Tasks

### Add New REST Endpoint

1. Create DTO (if needed): `dto/NewFeatureRequestDTO.java`
2. Create Controller method:

```java
@PostMapping("/api/newfeature")
public ResponseEntity<NewFeatureResponseDTO> create(@RequestBody NewFeatureRequestDTO dto) {
    NewFeatureResponseDTO response = service.create(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

3. Implement Service:

```java
@Service
public class NewFeatureServiceImpl implements NewFeatureService {
    @Override
    public NewFeatureResponseDTO create(NewFeatureRequestDTO dto) {
        // Business logic
    }
}
```

4. Test with Postman/curl

---

### Add New Thymeleaf Page

1. Create template: `src/main/resources/templates/pages/newpage.html`

```html
<html th:replace="~{fragments/base :: layout(~{::content})}">
<div th:fragment="content">
    <h1>New Page</h1>
    <!-- Content -->
</div>
</html>
```

2. Add controller method:

```java
@GetMapping("/pages/newpage")
public String newPage(Model model) {
    model.addAttribute("currentPage", "newpage");
    return "pages/newpage";
}
```

3. Test at: `http://localhost:8080/pages/newpage`

---

## 📊 Monitoring & Logs

### View Logs

```bash
# Console logs (when running with Maven)
./mvnw spring-boot:run

# Tail log file (if configured)
tail -f logs/spring-boot-logger.log
```

### Enable Debug Logging

```properties
# application.properties
logging.level.com.sca.smartcampusbackend=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

---

## 🐛 Troubleshooting Development Issues

See [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common issues.

**Quick Fixes**:

```bash
# Clean Maven cache
rm -rf ~/.m2/repository
./mvnw clean install

# Reset database
DROP DATABASE smartcampus;
CREATE DATABASE smartcampus;

# Clear IntelliJ cache
File → Invalidate Caches → Invalidate and Restart
```

---

## 📞 Need Help?

- **Code Questions**: Open issue with label `question`
- **Bug Reports**: See [CONTRIBUTING.md](CONTRIBUTING.md)
- **Feature Ideas**: Open issue with label `feature-request`

---

**Happy Coding! 🚀**

**Maintained by**: Team BholeChature  
**Last Updated**: February 2025
