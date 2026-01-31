package com.sca.smartcampusbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * StudentEnrollment Entity - Authoritative source for student course registrations
 * Maps to the 'student_enrollments' table in the database
 * 
 * This is the KEY TABLE for personalized timetables - all timetable data
 * is derived from which offerings a student is enrolled in.
 * 
 * @since 1.0.0
 */
@Entity
@Table(name = "student_enrollments",
       uniqueConstraints = @UniqueConstraint(
           name = "unique_enrollment",
           columnNames = {"user_id", "offering_id"}
       ))
@Data
public class StudentEnrollment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private CourseOffering offering;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnrollmentStatus status = EnrollmentStatus.ENROLLED;
    
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private LocalDateTime enrolledAt;
    
    /**
     * Automatically set enrollment timestamp
     */
    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }
    
    /**
     * Enrollment status enum
     */
    public enum EnrollmentStatus {
        ENROLLED,
        DROPPED,
        WAITLIST
    }
}
