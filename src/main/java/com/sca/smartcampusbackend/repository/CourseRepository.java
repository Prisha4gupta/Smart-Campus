package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Course entity
 * Provides database access methods for course operations
 * 
 * @since 1.0.0
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    /**
     * Find course by code
     * 
     * @param code Course code (e.g., "CS101")
     * @return Optional containing course if found
     */
    Optional<Course> findByCode(String code);

    /**
     * Check if course with code exists
     * 
     * @param code Course code
     * @return true if exists
     */
    boolean existsByCode(String code);

    /**
     * Search courses by title with pagination
     * 
     * @param title    Search query (partial match)
     * @param pageable Pagination information
     * @return Page of matching courses
     */
    Page<Course> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
