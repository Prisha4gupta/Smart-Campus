-- ==============================================================
-- Smart Campus Assistant - Sample Data
-- Migration: V2__insert_sample_data.sql
-- Description: Inserts sample faculty, courses, offerings, enrollments, and events
-- Author: Team Stack Underflow
-- Date: November 28, 2025
-- ==============================================================

-- ==============================================================
-- 1. FACULTY DATA
-- ==============================================================
INSERT INTO faculty (first_name, last_name, email, department, phone, is_active, created_at, updated_at) VALUES
('John', 'Smith', 'john.smith@iiitb.ac.in', 'Computer Science', '+91-9876543210', TRUE, NOW(), NOW()),
('Sarah', 'Johnson', 'sarah.johnson@iiitb.ac.in', 'Computer Science', '+91-9876543211', TRUE, NOW(), NOW()),
('Michael', 'Brown', 'michael.brown@iiitb.ac.in', 'Mathematics', '+91-9876543212', TRUE, NOW(), NOW()),
('Emily', 'Davis', 'emily.davis@iiitb.ac.in', 'Electronics', '+91-9876543213', TRUE, NOW(), NOW()),
('Robert', 'Wilson', 'robert.wilson@iiitb.ac.in', 'Computer Science', '+91-9876543214', TRUE, NOW(), NOW()),
('Jennifer', 'Taylor', 'jennifer.taylor@iiitb.ac.in', 'Mathematics', '+91-9876543215', TRUE, NOW(), NOW());

-- ==============================================================
-- 2. COURSES DATA
-- ==============================================================
INSERT INTO courses (code, title, credits, description, created_at) VALUES
('CS101', 'Introduction to Programming', 4, 'Fundamental programming concepts using Java', NOW()),
('CS201', 'Data Structures and Algorithms', 4, 'Advanced data structures, algorithm design and analysis', NOW()),
('CS301', 'Database Management Systems', 3, 'Relational databases, SQL, normalization, transactions', NOW()),
('CS302', 'Operating Systems', 3, 'Process management, memory management, file systems', NOW()),
('MA101', 'Linear Algebra', 3, 'Vector spaces, matrices, eigenvalues, applications', NOW()),
('MA201', 'Probability and Statistics', 3, 'Probability theory, statistical inference, hypothesis testing', NOW()),
('EC101', 'Digital Electronics', 3, 'Logic gates, combinational and sequential circuits', NOW()),
('CS401', 'Machine Learning', 4, 'Supervised/unsupervised learning, neural networks', NOW());

-- ==============================================================
-- 3. COURSE OFFERINGS (Fall 2024)
-- ==============================================================
INSERT INTO course_offerings (course_id, semester, section_code, faculty_id, capacity, created_at) VALUES
-- CS101 - Two sections
(1, 'Fall2024', 'A', 1, 60, NOW()),
(1, 'Fall2024', 'B', 2, 60, NOW()),

-- CS201 - One section
(2, 'Fall2024', 'A', 1, 50, NOW()),

-- CS301 - Two sections
(3, 'Fall2024', 'A', 2, 55, NOW()),
(3, 'Fall2024', 'B', 5, 55, NOW()),

-- CS302 - One section
(4, 'Fall2024', 'A', 5, 50, NOW()),

-- MA101 - Two sections
(5, 'Fall2024', 'A', 3, 70, NOW()),
(5, 'Fall2024', 'B', 6, 70, NOW()),

-- MA201 - One section
(6, 'Fall2024', 'A', 6, 60, NOW()),

-- EC101 - One section
(7, 'Fall2024', 'A', 4, 50, NOW()),

-- CS401 - One section (Advanced elective)
(8, 'Fall2024', 'A', 1, 40, NOW());

-- ==============================================================
-- 4. TIMETABLE ENTRIES
-- ==============================================================
-- Note: dayOfWeek: 1=Sunday, 2=Monday, 3=Tuesday, 4=Wednesday, 5=Thursday, 6=Friday, 7=Saturday

-- CS101 Section A (Monday/Wednesday/Friday 9:00-10:00, Room: LH101)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(1, 2, '09:00:00', '10:00:00', 'LH101'),  -- Monday
(1, 4, '09:00:00', '10:00:00', 'LH101'),  -- Wednesday
(1, 6, '09:00:00', '10:00:00', 'LH101');  -- Friday

-- CS101 Section B (Monday/Wednesday/Friday 10:30-11:30, Room: LH102)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(2, 2, '10:30:00', '11:30:00', 'LH102'),
(2, 4, '10:30:00', '11:30:00', 'LH102'),
(2, 6, '10:30:00', '11:30:00', 'LH102');

-- CS201 Section A (Tuesday/Thursday 14:00-15:30, Room: LH201)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(3, 3, '14:00:00', '15:30:00', 'LH201'),  -- Tuesday
(3, 5, '14:00:00', '15:30:00', 'LH201');  -- Thursday

-- CS301 Section A (Monday/Wednesday 14:00-15:30, Room: Lab1)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(4, 2, '14:00:00', '15:30:00', 'Lab1'),
(4, 4, '14:00:00', '15:30:00', 'Lab1');

-- CS301 Section B (Tuesday/Thursday 10:30-12:00, Room: Lab2)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(5, 3, '10:30:00', '12:00:00', 'Lab2'),
(5, 5, '10:30:00', '12:00:00', 'Lab2');

-- CS302 Section A (Monday/Thursday 16:00-17:30, Room: LH202)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(6, 2, '16:00:00', '17:30:00', 'LH202'),
(6, 5, '16:00:00', '17:30:00', 'LH202');

-- MA101 Section A (Tuesday/Thursday 09:00-10:30, Room: MB101)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(7, 3, '09:00:00', '10:30:00', 'MB101'),
(7, 5, '09:00:00', '10:30:00', 'MB101');

-- MA101 Section B (Monday/Wednesday 11:00-12:30, Room: MB102)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(8, 2, '11:00:00', '12:30:00', 'MB102'),
(8, 4, '11:00:00', '12:30:00', 'MB102');

-- MA201 Section A (Wednesday/Friday 14:00-15:30, Room: MB201)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(9, 4, '14:00:00', '15:30:00', 'MB201'),
(9, 6, '14:00:00', '15:30:00', 'MB201');

-- EC101 Section A (Tuesday/Friday 16:00-17:30, Room: EB101)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(10, 3, '16:00:00', '17:30:00', 'EB101'),
(10, 6, '16:00:00', '17:30:00', 'EB101');

-- CS401 Section A (Wednesday/Friday 10:30-12:00, Room: Lab3)
INSERT INTO timetable_entries (offering_id, day_of_week, start_time, end_time, room) VALUES
(11, 4, '10:30:00', '12:00:00', 'Lab3'),
(11, 6, '10:30:00', '12:00:00', 'Lab3');

-- ==============================================================
-- 5. SAMPLE STUDENT ENROLLMENTS
-- ==============================================================
-- Assuming user_id = 1 exists (created during signup)
-- Enrolling student in 5 courses for Fall 2024

-- Note: You may need to adjust user_id values based on your actual users table
-- This assumes at least one student user exists with id=1

-- INSERT INTO student_enrollments (user_id, offering_id, status, enrolled_at) VALUES
-- (1, 1, 'ENROLLED', NOW()),  -- CS101 Section A
-- (1, 3, 'ENROLLED', NOW()),  -- CS201 Section A
-- (1, 4, 'ENROLLED', NOW()),  -- CS301 Section A
-- (1, 7, 'ENROLLED', NOW()),  -- MA101 Section A
-- (1, 10, 'ENROLLED', NOW()); -- EC101 Section A

-- ==============================================================
-- 6. SAMPLE EVENTS
-- ==============================================================
-- Note: Adjust created_by user_id based on your admin user
-- These events are set for future dates

-- INSERT INTO events (title, body, created_by, is_public, start_datetime, end_datetime, created_at) VALUES
-- ('Tech Fest 2024', 'Annual technical festival with coding competitions and workshops', 1, TRUE, '2024-12-15 09:00:00', '2024-12-17 18:00:00', NOW()),
-- ('Midterm Exams Begin', 'Midterm examination period for all courses', 1, TRUE, '2024-12-01 09:00:00', '2024-12-07 17:00:00', NOW()),
-- ('Guest Lecture: AI Ethics', 'Distinguished speaker on ethical considerations in artificial intelligence', 1, TRUE, '2024-12-05 14:00:00', '2024-12-05 16:00:00', NOW()),
-- ('Career Fair 2024', 'Annual placement drive with top companies', 1, TRUE, '2024-12-10 10:00:00', '2024-12-10 17:00:00', NOW());

-- ==============================================================
-- NOTES:
-- ==============================================================
-- 1. Commented out enrollment and event inserts because they depend on users table
-- 2. Uncomment and adjust user IDs after creating test users via signup
-- 3. This data creates a realistic academic semester structure
-- 4. Timetable entries are realistic IIITB-style schedules
-- 5. Faculty emails follow institutional pattern
-- ==============================================================
