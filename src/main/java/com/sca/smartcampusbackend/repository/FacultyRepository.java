package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Faculty entity
 * Provides database access methods for faculty operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    
    /**
     * Find faculty by email
     * @param email Faculty email
     * @return Optional containing faculty if found
     */
    Optional<Faculty> findByEmail(String email);
    
    /**
     * Find all faculty by department
     * @param department Department name
     * @return List of faculty in the department
     */
    List<Faculty> findByDepartment(String department);
    
    /**
     * Find all active faculty
     * @param isActive Active status
     * @return List of active faculty
     */
    List<Faculty> findByIsActive(Boolean isActive);
    
    /**
     * Find faculty by department and active status
     * @param department Department name
     * @param isActive Active status
     * @return List of faculty matching criteria
     */
    List<Faculty> findByDepartmentAndIsActive(String department, Boolean isActive);
}
