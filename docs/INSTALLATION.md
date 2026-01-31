# 🚀 Installation Guide - Smart Campus Assistant

Complete step-by-step installation guide for setting up the Smart Campus Assistant on your local machine or server.

---

## 📋 Prerequisites

### Required Software

| Software | Version | Download Link |
|----------|---------|---------------|
| **Java JDK** | 24 or 25 | [OpenJDK](https://openjdk.org/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) |
| **Maven** | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| **MySQL** | 8.0+ | [MySQL Downloads](https://dev.mysql.com/downloads/mysql/) (or use Aiven Cloud) |
| **Git** | Latest | [Git Downloads](https://git-scm.com/downloads) |

### Optional Tools

- **IntelliJ IDEA** (Community or Ultimate) - Recommended IDE
- **VS Code** with Java extensions
- **Postman** - For API testing
- **MySQL Workbench** - For database management

---

## 🛠️ Installation Steps

### Step 1: Install Java

#### Windows:
1. Download Java 24/25 JDK from [OpenJDK](https://openjdk.org/)
2. Run installer and follow wizard
3. Set `JAVA_HOME` environment variable:
   - Right-click "This PC" → Properties → Advanced System Settings
   - Environment Variables → New System Variable
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-24` (your JDK path)
4. Add to `PATH`: `%JAVA_HOME%\bin`
5. Verify: `java --version` in CMD

#### macOS:
```bash
# Using Homebrew
brew install openjdk@24

# Add to PATH (add to ~/.zshrc or ~/.bash_profile)
export JAVA_HOME=$(/usr/libexec/java_home -v 24)
export PATH="$JAVA_HOME/bin:$PATH"

# Reload shell
source ~/.zshrc

# Verify
java --version
```

#### Linux (Ubuntu/Debian):
```bash
# Install OpenJDK
sudo apt update
sudo apt install openjdk-24-jdk -y

# Set JAVA_HOME (add to ~/.bashrc)
export JAVA_HOME=/usr/lib/jvm/java-24-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

# Reload
source ~/.bashrc

# Verify
java --version
```

---

### Step 2: Install Maven

#### Windows:
1. Download Maven from [Apache Maven](https://maven.apache.org/download.cgi)
2. Extract to `C:\Program Files\Maven`
3. Add to `PATH`: `C:\Program Files\Maven\bin`
4. Verify: `mvn --version`

#### macOS:
```bash
brew install maven
mvn --version
```

#### Linux:
```bash
sudo apt install maven -y
mvn --version
```

---

### Step 3: Install MySQL (or use Aiven Cloud)

#### Option A: Local MySQL Installation

**Windows**:
1. Download MySQL Installer from [MySQL Downloads](https://dev.mysql.com/downloads/installer/)
2. Run installer, choose "Developer Default"
3. Set root password during installation
4. Note the port (default: 3306)

**macOS**:
```bash
brew install mysql
brew services start mysql
mysql_secure_installation
```

**Linux**:
```bash
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo mysql_secure_installation
```

**Create Database**:
```sql
mysql -u root -p
CREATE DATABASE smartcampus;
CREATE USER 'scauser'@'localhost' IDENTIFIED BY 'scapassword';
GRANT ALL PRIVILEGES ON smartcampus.* TO 'scauser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

#### Option B: Use Aiven Cloud MySQL

1. Sign up at [Aiven](https://aiven.io/)
2. Create a MySQL 8.0 service
3. Note down connection details:
   - Host
   - Port
   - Username
   - Password
   - Database name

---

### Step 4: Clone Repository

```bash
# Clone the repository
git clone https://github.com/Jdsb06/SmartCampus.git

# Navigate to backend folder
cd SmartCampus/smart-campus-backend

# Verify files
ls -la
```

---

### Step 5: Configure Database Connection

Edit `src/main/resources/application.properties`:

#### For Local MySQL:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/smartcampus?useSSL=false&serverTimezone=UTC
spring.datasource.username=scauser
spring.datasource.password=scapassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# Thymeleaf Configuration
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Server Configuration
server.port=8080
server.error.whitelabel.enabled=false
```

#### For Aiven Cloud MySQL:
```properties
# Database Configuration (Aiven Cloud)
spring.datasource.url=jdbc:mysql://mysql-xxxxx-project.aivencloud.com:12345/defaultdb?sslMode=REQUIRED
spring.datasource.username=avnadmin
spring.datasource.password=YOUR_AIVEN_PASSWORD_HERE

# Rest of the configuration remains same as above
```

**Note**: Never commit passwords to Git. Use environment variables for production:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

---

### Step 6: Build the Project

```bash
# Clean and install dependencies
./mvnw clean install

# For Windows:
mvnw.cmd clean install

# Build should complete without errors
# Output: BUILD SUCCESS
```

**Common Build Issues**:

| Issue | Solution |
|-------|----------|
| "JAVA_HOME not set" | Set JAVA_HOME environment variable |
| "Maven not found" | Add Maven to PATH |
| "Dependencies download failed" | Check internet connection, retry |
| "Tests failed" | Skip tests: `./mvnw clean install -DskipTests` |

---

### Step 7: Run Database Migrations

Flyway will automatically run migrations on application startup. To verify:

```bash
# Check migration scripts
ls src/main/resources/db/migration/

# You should see:
# V1__create_mvp_tables.sql
# V2__insert_sample_data.sql
# V3__create_groups_tables.sql
```

---

### Step 8: Run the Application

#### Option A: Using Maven

```bash
# Run Spring Boot application
./mvnw spring-boot:run

# For Windows:
mvnw.cmd spring-boot:run

# Wait for application to start
# Output: "Started SmartCampusBackendApplication in X seconds"
```

#### Option B: Using JAR

```bash
# Build JAR
./mvnw clean package -DskipTests

# Run JAR
java -jar target/smart-campus-backend-0.0.1-SNAPSHOT.jar
```

#### Option C: Using IDE (IntelliJ IDEA)

1. Open project in IntelliJ
2. Wait for Maven import
3. Find `SmartCampusBackendApplication.java`
4. Right-click → Run 'SmartCampusBackendApplication'

---

### Step 9: Access the Application

Open your browser and navigate to:

- **Login Page**: http://localhost:8080/login
- **Signup Page**: http://localhost:8080/signup

**Default Admin Credentials** (from sample data):
```
Username: admin
Password: admin123
```

**Default Student Credentials**:
```
Username: IMT2022001
Password: password123
```

---

### Step 10: Verify Installation

#### Check Database Tables

```bash
# Connect to MySQL
mysql -u scauser -p smartcampus

# List tables
SHOW TABLES;

# Should show:
# users, faculty, courses, course_offerings, 
# student_enrollments, timetable_entries, events, notifications
```

#### Check Application Logs

```bash
# Watch logs for errors
tail -f logs/spring-boot-logger.log

# Or check console output
```

#### Test REST API

```bash
# Using curl (replace with valid session cookie after login)
curl -X GET http://localhost:8080/api/courses \
  -H "Cookie: JSESSIONID=your-session-id"

# Or use Postman/Insomnia
```

---

## 🔧 Configuration Options

### Change Server Port

```properties
# application.properties
server.port=9090
```

### Enable Debug Logs

```properties
# application.properties
logging.level.com.sca.smartcampusbackend=DEBUG
logging.level.org.springframework.security=DEBUG
```

### Configure Maximum Upload Size

```properties
# For file uploads (future feature)
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

---

## 🐛 Troubleshooting

### Issue: Port 8080 Already in Use

**Solution**:
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill process
kill -9 <PID>  # macOS/Linux
taskkill /PID <PID> /F  # Windows

# Or change port in application.properties
```

---

### Issue: Database Connection Failed

**Solution**:
1. Verify MySQL is running: `systemctl status mysql` (Linux)
2. Check credentials in `application.properties`
3. Test connection:
   ```bash
   mysql -u scauser -p -h localhost
   ```
4. Check firewall rules (port 3306)

---

### Issue: Flyway Migration Failed

**Solution**:
```bash
# Repair Flyway schema
./mvnw flyway:repair

# Or reset database (CAUTION: deletes all data)
DROP DATABASE smartcampus;
CREATE DATABASE smartcampus;
```

---

### Issue: Login Page Returns 404

**Solution**:
1. Check `PageController.java` has `@GetMapping("/login")`
2. Verify `login.html` exists in `src/main/resources/templates/`
3. Check application started without errors
4. Access full URL: `http://localhost:8080/login`

---

### Issue: CSRF Token Missing

**Solution**:
1. Verify `<meta>` tags in templates:
   ```html
   <meta name="_csrf" th:content="${_csrf.token}"/>
   <meta name="_csrf_header" th:content="${_csrf.headerName}"/>
   ```
2. Check JavaScript includes CSRF in requests
3. Disable CSRF for testing (not recommended):
   ```java
   http.csrf().disable()
   ```

---

## 🔒 Production Deployment Checklist

- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/SSL
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (not `update`)
- [ ] Disable debug logs
- [ ] Configure proper logging (file rotation)
- [ ] Set up database backups
- [ ] Configure firewall rules
- [ ] Use strong passwords
- [ ] Enable CSRF protection
- [ ] Set `server.error.whitelabel.enabled=false`
- [ ] Configure session timeout
- [ ] Set up monitoring/alerting

---

## 📞 Need Help?

**Installation Issues**:
- Check [Troubleshooting Guide](TROUBLESHOOTING.md)
- Open issue: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues)
- Tag: `installation`

**Contact**: Team BholeChature

---

**Last Updated**: February 2025  
**Maintained by**: Team BholeChature
