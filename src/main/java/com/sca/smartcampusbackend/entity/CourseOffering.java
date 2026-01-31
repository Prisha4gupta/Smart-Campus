package com.sca.smartcampusbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * CourseOffering Entity - Represents specific course sections per semester
 */
@Entity
@Table(name = "course_offerings", uniqueConstraints = @UniqueConstraint(name = "unique_offering", columnNames = {
        "course_id", "semester", "section_code" }))
@Data
public class CourseOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false, length = 50)
    private String semester;

    @Column(name = "section_code", nullable = false, length = 10)
    private String sectionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private Integer capacity = 60;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
