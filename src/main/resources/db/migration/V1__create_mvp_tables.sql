-- ==============================================================
-- Smart Campus Assistant - MVP Database Schema
-- Migration: V1__create_mvp_tables.sql
-- Description: Creates core tables for personalized timetables,
--              electives, enrollments, events, and notifications
-- Author: Team Stack Underflow
-- Date: November 28, 2025
-- ==============================================================

-- ==============================================================
-- 1. FACULTY TABLE
-- Stores instructor information
-- ==============================================================
CREATE TABLE IF NOT EXISTS faculty (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    department VARCHAR(100),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_faculty_email (email),
    INDEX idx_faculty_department (department),
    INDEX idx_faculty_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 2. COURSES TABLE
-- Canonical course data (code, title, credits)
-- ==============================================================
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_course_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 3. COURSE_OFFERINGS TABLE
-- Represents specific course sections/electives per semester
-- ==============================================================
CREATE TABLE IF NOT EXISTS course_offerings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    semester VARCHAR(50) NOT NULL COMMENT 'e.g., Fall2024, Spring2025',
    section_code VARCHAR(10) NOT NULL COMMENT 'e.g., A, B, L1',
    faculty_id BIGINT NOT NULL,
    capacity INT NOT NULL DEFAULT 60,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE RESTRICT,
    
    UNIQUE KEY unique_offering (course_id, semester, section_code),
    INDEX idx_offering_semester (semester),
    INDEX idx_offering_faculty (faculty_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 4. STUDENT_ENROLLMENTS TABLE
-- Authoritative source for which classes each student takes
-- ==============================================================
CREATE TABLE IF NOT EXISTS student_enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    offering_id BIGINT NOT NULL,
    status ENUM('ENROLLED', 'DROPPED', 'WAITLIST') NOT NULL DEFAULT 'ENROLLED',
    enrolled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (offering_id) REFERENCES course_offerings(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_enrollment (user_id, offering_id),
    INDEX idx_enrollment_user (user_id),
    INDEX idx_enrollment_offering (offering_id),
    INDEX idx_enrollment_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 5. TIMETABLE_ENTRIES TABLE
-- Actual meeting times for course offerings (derived timetable)
-- ==============================================================
CREATE TABLE IF NOT EXISTS timetable_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    offering_id BIGINT NOT NULL,
    day_of_week TINYINT NOT NULL COMMENT '1=Sunday, 2=Monday, ..., 7=Saturday',
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(50) NOT NULL,
    
    FOREIGN KEY (offering_id) REFERENCES course_offerings(id) ON DELETE CASCADE,
    
    INDEX idx_timetable_offering (offering_id),
    INDEX idx_timetable_day_time (day_of_week, start_time),
    
    CONSTRAINT chk_day_of_week CHECK (day_of_week BETWEEN 1 AND 7),
    CONSTRAINT chk_time_order CHECK (start_time < end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 6. EVENTS TABLE (MVP VERSION)
-- Simple events for announcements and campus activities
-- ==============================================================
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    body TEXT,
    created_by BIGINT NOT NULL COMMENT 'User ID who created the event',
    is_public BOOLEAN DEFAULT TRUE COMMENT 'Public events visible to all',
    start_datetime DATETIME NOT NULL,
    end_datetime DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE,
    
    INDEX idx_event_start (start_datetime),
    INDEX idx_event_public (is_public),
    INDEX idx_event_creator (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- 7. NOTIFICATIONS TABLE
-- Per-user notifications (supports idempotent generation)
-- ==============================================================
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_id BIGINT NULL COMMENT 'Nullable - links to event if applicable',
    title VARCHAR(255) NOT NULL,
    body TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    delivery_channel ENUM('IN_APP', 'EMAIL', 'PUSH') NOT NULL DEFAULT 'IN_APP',
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    
    UNIQUE KEY unique_user_event (user_id, event_id),
    INDEX idx_notification_user_created (user_id, created_at),
    INDEX idx_notification_read (read_at),
    INDEX idx_notification_event (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==============================================================
-- DATA INTEGRITY & PERFORMANCE NOTES
-- ==============================================================
-- 1. All FK relationships use ON DELETE CASCADE or RESTRICT appropriately
-- 2. Indexes added for common query patterns (enrollment lookups, timetable resolution)
-- 3. Unique constraints ensure data integrity (no duplicate enrollments)
-- 4. ENUM types for status fields prevent invalid states
-- 5. CHECK constraints enforce business rules (time ordering, day ranges)
-- 6. Notifications table supports idempotency via UNIQUE(user_id, event_id)
-- ==============================================================
