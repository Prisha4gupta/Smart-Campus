# 🗄️ Database Schema Documentation

Complete database schema documentation for Smart Campus Assistant, including table structures, relationships, constraints, and ER diagrams.

**Last Updated**: December 12, 2025  
**Schema Version**: 2.1 (Flyway V6)  
**Database**: MySQL 8.0 on Aiven Cloud  
**Total Tables**: 13 (12 core + 1 legacy)

---

## 📊 Entity-Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         DATABASE SCHEMA                             │
│                    Smart Campus Assistant v2.1                      │
│         Including Quiz Review & Messaging Modules                   │
└─────────────────────────────────────────────────────────────────────┘

┌──────────────┐                ┌───────────────────┐
│    users     │                │     faculty       │
├──────────────┤                ├───────────────────┤
│ id (PK)      │                │ id (PK)           │
│ username     │                │ first_name        │
│ password     │                │ last_name         │
│ role         │                │ email             │
│ full_name    │                │ phone             │
│ email        │                │ department        │
│ created_at   │                │ cabin_no          │
└──────┬───────┘                │ is_active         │
       │ 1                      └───────┬───────────┘
       │                                │ 1
       │ *                              │ *
┌──────┴───────────┐         ┌──────────┴──────────────┐
│  notifications   │         │  course_offerings       │
├──────────────────┤         ├─────────────────────────┤
│ id (PK)          │         │ id (PK)                 │
│ user_id (FK)     │         │ course_id (FK)          │
│ title            │         │ faculty_id (FK)         │
│ message          │         │ semester                │
│ type             │         │ year                    │
│ is_read          │         │ enrolled_count          │
│ created_at       │         │ max_capacity            │
└──────────────────┘         │ created_at              │
                             └──┬───┬───────┬──────────┘
       ┌─────────────────────────┘   │       │
       │ 1                            │ *     │ *
┌──────┴──────────────┐     ┌─────────┴────┐ │
│  timetable_entries  │     │     quiz     │ │
├─────────────────────┤     ├──────────────┤ │
│ id (PK)             │     │ id (PK)      │ │
│ offering_id (FK)    │     │ offering_id  │ │
│ day_of_week         │     │ title        │ │
│ start_time          │     │ description  │ │
│ end_time            │     │ date         │ │
│ room_no             │     │ max_marks    │ │
│ class_type          │     └──┬────────┬──┘ │
└─────────────────────┘        │ 1      │ 1  │ 1
                               │ *      │ *  │
                      ┌────────┴─┐  ┌──┴────┴────────────┐
                      │quiz_     │  │ student_enrollments│
                      │question  │  ├────────────────────┤
                      ├──────────┤  │ id (PK)            │
                      │ id (PK)  │  │ user_id (FK)       │
                      │ quiz_id  │  │ offering_id (FK)   │
                      │ question │  │ enrolled_at        │
                      │ _number  │  │ grade              │
                      │ text     │  └────────────────────┘
                      │ max_marks│
                      └──┬───────┘       ┌──────────────┐
                         │ 1             │   message    │
                         │ *             ├──────────────┤
                  ┌──────┴──────┐        │ id (PK)      │
                  │quiz_answer  │        │ sender_id    │
                  ├─────────────┤        │ receiver_id  │
                  │ id (PK)     │        │ content      │
                  │ attempt_id  │        │ sent_at      │
                  │ question_id │        │ is_read      │
                  │ obtained_   │        │ is_broadcast │
                  │ marks       │        └──────────────┘
                  │ evaluator_  │
                  │ comment     │
                  └──┬──────────┘
                     │ *
                     │ 1
              ┌──────┴─────────┐
              │ quiz_attempt   │
              ├────────────────┤
              │ id (PK)        │
              │ quiz_id (FK)   │
              │ user_id (FK)   │
              │ obtained_marks │
              │ status         │
              └────────────────┘

┌──────────────────┐              ┌───────────────┐
│     courses      │◄─────────────│    events     │
├──────────────────┤     (linked) ├───────────────┤
│ id (PK)          │              │ id (PK)       │
│ code             │              │ title         │
│ title            │              │ description   │
│ description      │              │ start_datetime│
│ credits          │              │ end_datetime  │
│ department       │              │ venue         │
│ semester         │              │ is_public     │
│ created_at       │              │ created_by_id │
└──────────────────┘              │ created_at    │
                                  └───────────────┘
```

---

## 📋 Table Definitions

### 1. `users` Table

**Purpose**: Stores authentication credentials and basic user information.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL | Login username (typically SRN) |
| `password` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `role` | VARCHAR(20) | NOT NULL | User role: 'STUDENT' or 'ADMIN' |
| `full_name` | VARCHAR(100) | NULL | User's full name |
| `email` | VARCHAR(100) | UNIQUE, NULL | Email address |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Account creation time |

**Indexes**:
```sql
CREATE UNIQUE INDEX idx_users_username ON users(username);
CREATE UNIQUE INDEX idx_users_email ON users(email);
```

**Example Data**:
```sql
INSERT INTO users (username, password, role, full_name, email) VALUES
('IMT2022001', '$2a$10$...', 'STUDENT', 'John Doe', 'john.doe@iiitb.ac.in'),
('admin', '$2a$10$...', 'ADMIN', 'Admin User', 'admin@iiitb.ac.in');
```

---

### 2. `faculty` Table

**Purpose**: Stores faculty member profiles and contact information.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique faculty identifier |
| `first_name` | VARCHAR(50) | NOT NULL | First name |
| `last_name` | VARCHAR(50) | NOT NULL | Last name |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL | Official email |
| `phone` | VARCHAR(20) | NULL | Contact number |
| `department` | VARCHAR(50) | NOT NULL | Department (CSE, ECE, etc.) |
| `cabin_no` | VARCHAR(20) | NULL | Office/cabin number |
| `is_active` | BOOLEAN | DEFAULT TRUE | Active status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

**Indexes**:
```sql
CREATE INDEX idx_faculty_department ON faculty(department);
CREATE UNIQUE INDEX idx_faculty_email ON faculty(email);
```

**Example Data**:
```sql
INSERT INTO faculty (first_name, last_name, email, department, cabin_no) VALUES
('Dr. Rajesh', 'Kumar', 'rajesh.kumar@iiitb.ac.in', 'CSE', 'A-201'),
('Dr. Priya', 'Sharma', 'priya.sharma@iiitb.ac.in', 'ECE', 'B-105');
```

---

### 3. `courses` Table

**Purpose**: Master table for all courses offered by the institution.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique course identifier |
| `code` | VARCHAR(20) | UNIQUE, NOT NULL | Course code (e.g., CS101) |
| `title` | VARCHAR(150) | NOT NULL | Course title |
| `description` | TEXT | NULL | Course description |
| `credits` | INTEGER | NOT NULL | Credit hours (1-6) |
| `department` | VARCHAR(50) | NOT NULL | Owning department |
| `semester` | INTEGER | NULL | Recommended semester (1-8) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

**Indexes**:
```sql
CREATE UNIQUE INDEX idx_courses_code ON courses(code);
CREATE INDEX idx_courses_department ON courses(department);
CREATE INDEX idx_courses_semester ON courses(semester);
```

**Example Data**:
```sql
INSERT INTO courses (code, title, credits, department, semester) VALUES
('CS101', 'Introduction to Programming', 4, 'CSE', 1),
('CS201', 'Data Structures and Algorithms', 4, 'CSE', 2),
('EC101', 'Digital Electronics', 3, 'ECE', 1);
```

---

### 4. `course_offerings` Table

**Purpose**: Links courses to faculty for a specific semester/year.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique offering identifier |
| `course_id` | BIGINT | FOREIGN KEY → courses(id), NOT NULL | Course being offered |
| `faculty_id` | BIGINT | FOREIGN KEY → faculty(id), NOT NULL | Faculty teaching the course |
| `semester` | INTEGER | NOT NULL | Semester (1=Odd, 2=Even) |
| `year` | INTEGER | NOT NULL | Academic year (e.g., 2025) |
| `enrolled_count` | INTEGER | DEFAULT 0 | Current enrollment count |
| `max_capacity` | INTEGER | NOT NULL | Maximum enrollment capacity |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

**Constraints**:
```sql
ALTER TABLE course_offerings
ADD CONSTRAINT fk_offering_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_offering_faculty FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE SET NULL,
ADD CONSTRAINT chk_capacity CHECK (enrolled_count <= max_capacity);
```

**Indexes**:
```sql
CREATE INDEX idx_offerings_course ON course_offerings(course_id);
CREATE INDEX idx_offerings_faculty ON course_offerings(faculty_id);
CREATE INDEX idx_offerings_semester_year ON course_offerings(semester, year);
```

**Example Data**:
```sql
INSERT INTO course_offerings (course_id, faculty_id, semester, year, max_capacity) VALUES
(1, 1, 1, 2025, 60),  -- CS101 taught by Dr. Rajesh in Odd 2025
(2, 1, 2, 2025, 50);  -- CS201 taught by Dr. Rajesh in Even 2025
```

---

### 5. `student_enrollments` Table

**Purpose**: Tracks student enrollments in course offerings.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique enrollment identifier |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Student user ID |
| `offering_id` | BIGINT | FOREIGN KEY → course_offerings(id), NOT NULL | Course offering ID |
| `enrolled_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Enrollment timestamp |
| `grade` | VARCHAR(2) | NULL | Final grade (A+, A, B+, etc.) |

**Constraints**:
```sql
ALTER TABLE student_enrollments
ADD CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
ADD CONSTRAINT fk_enrollment_offering FOREIGN KEY (offering_id) REFERENCES course_offerings(id) ON DELETE CASCADE,
ADD CONSTRAINT uk_enrollment UNIQUE (user_id, offering_id);  -- Prevent duplicate enrollments
```

**Indexes**:
```sql
CREATE INDEX idx_enrollments_user ON student_enrollments(user_id);
CREATE INDEX idx_enrollments_offering ON student_enrollments(offering_id);
```

**Example Data**:
```sql
INSERT INTO student_enrollments (user_id, offering_id) VALUES
(1, 1),  -- Student 1 enrolls in CS101
(1, 2);  -- Student 1 enrolls in CS201
```

---

### 6. `timetable_entries` Table

**Purpose**: Stores scheduled class timings for course offerings.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique entry identifier |
| `offering_id` | BIGINT | FOREIGN KEY → course_offerings(id), NOT NULL | Associated course offering |
| `day_of_week` | VARCHAR(10) | NOT NULL | Day (MONDAY, TUESDAY, etc.) |
| `start_time` | TIME | NOT NULL | Class start time |
| `end_time` | TIME | NOT NULL | Class end time |
| `room_no` | VARCHAR(20) | NULL | Room/location |
| `class_type` | VARCHAR(20) | DEFAULT 'LECTURE' | Type: LECTURE, LAB, TUTORIAL |

**Constraints**:
```sql
ALTER TABLE timetable_entries
ADD CONSTRAINT fk_timetable_offering FOREIGN KEY (offering_id) REFERENCES course_offerings(id) ON DELETE CASCADE,
ADD CONSTRAINT chk_time CHECK (end_time > start_time);
```

**Indexes**:
```sql
CREATE INDEX idx_timetable_offering ON timetable_entries(offering_id);
CREATE INDEX idx_timetable_day ON timetable_entries(day_of_week);
```

**Example Data**:
```sql
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room_no, class_type) VALUES
(1, 'MONDAY', '09:00:00', '10:30:00', 'A-301', 'LECTURE'),
(1, 'WEDNESDAY', '09:00:00', '10:30:00', 'A-301', 'LECTURE'),
(2, 'TUESDAY', '11:00:00', '12:30:00', 'B-201', 'LECTURE');
```

---

### 7. `events` Table

**Purpose**: Stores campus events and announcements.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique event identifier |
| `title` | VARCHAR(150) | NOT NULL | Event title |
| `description` | TEXT | NULL | Event description |
| `start_datetime` | TIMESTAMP | NOT NULL | Event start date/time |
| `end_datetime` | TIMESTAMP | NULL | Event end date/time |
| `venue` | VARCHAR(100) | NULL | Event location |
| `is_public` | BOOLEAN | DEFAULT TRUE | Public visibility flag |
| `created_by_id` | BIGINT | FOREIGN KEY → users(id), NULL | Creator user ID (admin) |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Record creation time |

**Constraints**:
```sql
ALTER TABLE events
ADD CONSTRAINT fk_event_creator FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE SET NULL;
```

**Indexes**:
```sql
CREATE INDEX idx_events_is_public ON events(is_public);
CREATE INDEX idx_events_start_datetime ON events(start_datetime);
CREATE INDEX idx_events_creator ON events(created_by_id);
```

**Example Data**:
```sql
INSERT INTO events (title, description, start_datetime, venue, is_public, created_by_id) VALUES
('Tech Fest 2025', 'Annual technology festival', '2025-03-15 09:00:00', 'Auditorium', TRUE, 2),
('Faculty Meeting', 'Internal faculty discussion', '2025-02-20 14:00:00', 'Conference Room', FALSE, 2);
```

---

### 8. `notifications` Table

**Purpose**: Stores user notifications for various events.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique notification identifier |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Recipient user ID |
| `title` | VARCHAR(100) | NOT NULL | Notification title |
| `message` | TEXT | NOT NULL | Notification message |
| `type` | VARCHAR(20) | DEFAULT 'INFO' | Type: INFO, WARNING, SUCCESS, ERROR |
| `is_read` | BOOLEAN | DEFAULT FALSE | Read status |
| `created_at` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Creation timestamp |

**Constraints**:
```sql
ALTER TABLE notifications
ADD CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

**Indexes**:
```sql
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at DESC);
```

**Example Data**:
```sql
INSERT INTO notifications (user_id, title, message, type) VALUES
(1, 'Enrollment Successful', 'You have successfully enrolled in CS101', 'SUCCESS'),
(1, 'Upcoming Event', 'Tech Fest 2025 starts in 2 days', 'INFO');
```

---

### 9. `quiz` Table

**Purpose**: Stores quiz metadata for course offerings (V6 migration).

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique quiz identifier |
| `offering_id` | BIGINT | FOREIGN KEY → course_offerings(id), NOT NULL | Associated course offering |
| `title` | VARCHAR(255) | NOT NULL | Quiz title |
| `description` | TEXT | NULL | Quiz description/instructions |
| `date` | DATETIME | NOT NULL | Quiz date/time |
| `max_marks` | INT | NOT NULL | Total maximum marks |

**Constraints**:
```sql
ALTER TABLE quiz
ADD CONSTRAINT fk_quiz_offering FOREIGN KEY (offering_id) REFERENCES course_offerings(id) 
    ON UPDATE CASCADE ON DELETE RESTRICT;
```

**Example Data**:
```sql
INSERT INTO quiz (offering_id, title, description, date, max_marks) VALUES
(1, 'Midterm Exam', 'Covers chapters 1-5', '2025-03-15 10:00:00', 100);
```

---

### 10. `quiz_question` Table

**Purpose**: Stores individual questions within a quiz.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique question identifier |
| `quiz_id` | BIGINT | FOREIGN KEY → quiz(id), NOT NULL | Parent quiz |
| `question_number` | INT | NOT NULL | Question sequence number |
| `max_marks` | INT | NOT NULL | Maximum marks for this question |
| `text` | TEXT | NOT NULL | Question text/prompt |

**Constraints**:
```sql
ALTER TABLE quiz_question
ADD CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quiz(id) 
    ON UPDATE CASCADE ON DELETE RESTRICT;
```

**Example Data**:
```sql
INSERT INTO quiz_question (quiz_id, question_number, max_marks, text) VALUES
(1, 1, 10, 'Explain polymorphism in OOP'),
(1, 2, 15, 'Implement binary search algorithm');
```

---

### 11. `quiz_attempt` Table

**Purpose**: Tracks student attempts on quizzes.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique attempt identifier |
| `quiz_id` | BIGINT | FOREIGN KEY → quiz(id), NOT NULL | Quiz being attempted |
| `user_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Student attempting |
| `obtained_marks` | INT | NULL | Total marks obtained (after grading) |
| `status` | VARCHAR(20) | NOT NULL | Status: NOT_ATTEMPTED, PENDING, GRADED |

**Constraints**:
```sql
ALTER TABLE quiz_attempt
ADD CONSTRAINT uq_attempt_quiz_user UNIQUE (quiz_id, user_id);  -- One attempt per student per quiz
```

**Example Data**:
```sql
INSERT INTO quiz_attempt (quiz_id, user_id, obtained_marks, status) VALUES
(1, 1, 85, 'GRADED');
```

---

### 12. `quiz_answer` Table

**Purpose**: Stores question-wise answers and grades.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique answer identifier |
| `attempt_id` | BIGINT | FOREIGN KEY → quiz_attempt(id), NOT NULL | Parent attempt |
| `question_id` | BIGINT | FOREIGN KEY → quiz_question(id), NOT NULL | Question being answered |
| `obtained_marks` | INT | NULL | Marks awarded |
| `evaluator_comment` | TEXT | NULL | Faculty feedback |

**Example Data**:
```sql
INSERT INTO quiz_answer (attempt_id, question_id, obtained_marks, evaluator_comment) VALUES
(1, 1, 8, 'Good explanation, needs more detail');
```

---

### 13. `message` Table

**Purpose**: Student-admin messaging and broadcasts.

**Columns**:

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| `id` | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique message identifier |
| `sender_id` | BIGINT | FOREIGN KEY → users(id), NOT NULL | Message sender |
| `receiver_id` | BIGINT | FOREIGN KEY → users(id), NULL | Recipient (NULL for broadcast) |
| `content` | TEXT | NOT NULL | Message content |
| `sent_at` | DATETIME | NOT NULL | Timestamp when sent |
| `is_read` | BOOLEAN | DEFAULT FALSE | Read status |
| `is_broadcast` | BOOLEAN | DEFAULT FALSE | Broadcast flag |

**Example Data**:
```sql
INSERT INTO message (sender_id, receiver_id, content, sent_at, is_broadcast) VALUES
(2, 1, 'Welcome to Smart Campus!', NOW(), FALSE),
(2, NULL, 'Classes resume on Monday', NOW(), TRUE);
```

---

## 🔗 Relationships Summary

| Relationship | Type | Description |
|--------------|------|-------------|
| `users` → `student_enrollments` | One-to-Many | A user can have multiple enrollments |
| `users` → `notifications` | One-to-Many | A user can have multiple notifications |
| `users` → `events` | One-to-Many | An admin can create multiple events |
| `users` → `quiz_attempt` | One-to-Many | A user can attempt multiple quizzes |
| `users` → `message` (sender) | One-to-Many | A user can send multiple messages |
| `users` → `message` (receiver) | One-to-Many | A user can receive multiple messages |
| `courses` → `course_offerings` | One-to-Many | A course can have multiple offerings |
| `faculty` → `course_offerings` | One-to-Many | A faculty can teach multiple offerings |
| `course_offerings` → `student_enrollments` | One-to-Many | An offering can have multiple enrollments |
| `course_offerings` → `timetable_entries` | One-to-Many | An offering can have multiple timetable slots |
| `course_offerings` → `quiz` | One-to-Many | An offering can have multiple quizzes |
| `quiz` → `quiz_question` | One-to-Many | A quiz can have multiple questions |
| `quiz` → `quiz_attempt` | One-to-Many | A quiz can have multiple attempts |
| `quiz_attempt` → `quiz_answer` | One-to-Many | An attempt has answers for multiple questions |
| `quiz_question` → `quiz_answer` | One-to-Many | A question can have answers in multiple attempts |

---

## 📊 Database Constraints

### Primary Keys
All tables have auto-incrementing `BIGINT` primary keys named `id`.

### Foreign Keys
- **ON DELETE CASCADE**: Deleting parent deletes children (user → enrollments)
- **ON DELETE SET NULL**: Deleting parent sets FK to NULL (faculty → offerings)

### Unique Constraints
- `users.username`, `users.email`
- `faculty.email`
- `courses.code`
- `(user_id, offering_id)` in `student_enrollments`

### Check Constraints
- `enrolled_count <= max_capacity` in `course_offerings`
- `end_time > start_time` in `timetable_entries`

---

## 🗂️ Flyway Migration Scripts

**Location**: `src/main/resources/db/migration/`

### V1__create_mvp_tables.sql
Creates all 8 core tables with constraints.

### V2__insert_sample_data.sql
Inserts sample data for development/testing.

### V5__fix_notification_unique_constraint.sql
Fixes notification unique constraint for event-based notifications.

### V6__quiz_module.sql
Creates quiz tables: `quiz`, `quiz_question`, `quiz_attempt`, `quiz_answer`.

**Note**: Message table added directly to production database (not yet in migration).

**Migration Naming Convention**: `V{version}__{description}.sql`

---

## 📈 Query Optimization Tips

### Use Indexes for Frequent Queries
```sql
-- Optimize: Find student enrollments
SELECT * FROM student_enrollments WHERE user_id = ?;
-- Index: idx_enrollments_user

-- Optimize: Find offerings by faculty
SELECT * FROM course_offerings WHERE faculty_id = ?;
-- Index: idx_offerings_faculty
```

### Use JOINs Efficiently
```sql
-- Get student's timetable
SELECT te.*, c.code, c.title, f.first_name, f.last_name
FROM timetable_entries te
JOIN course_offerings co ON te.offering_id = co.id
JOIN courses c ON co.course_id = c.id
JOIN faculty f ON co.faculty_id = f.id
JOIN student_enrollments se ON se.offering_id = co.id
WHERE se.user_id = ?;
```

### Avoid N+1 Queries
Use `@EntityGraph` or JdbcTemplate with custom queries.

---

## 🔧 Database Maintenance

### Backup Strategy
- **Aiven Automated Backups**: Daily backups retained for 7 days
- **Manual Backups**: Before major migrations

### Monitoring
- Track query performance
- Monitor connection pool usage
- Set up alerts for slow queries (> 1s)

---

## 📞 Database Questions?

For database-related questions:
- Open issue with label `database`
- Contact: [GitHub Issues](https://github.com/Jdsb06/SmartCampus/issues)

---

**Last Updated**: December 12, 2025  
**Maintained by**: Team BholeChature
