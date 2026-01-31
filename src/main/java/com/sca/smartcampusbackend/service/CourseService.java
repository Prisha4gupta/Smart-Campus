package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.CourseRequestDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;

import java.util.List;

/**
 * Service interface for Course operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
public interface CourseService {
    
    /**
     * Create a new course
     * @param dto Course request DTO
     * @return Created course response DTO
     */
    CourseResponseDTO createCourse(CourseRequestDTO dto);
    
    /**
     * Update existing course
     * @param id Course ID
     * @param dto Course request DTO
     * @return Updated course response DTO
     */
    CourseResponseDTO updateCourse(Integer id, CourseRequestDTO dto);
    
    /**
     * Get course by ID
     * @param id Course ID
     * @return Course response DTO
     */
    CourseResponseDTO getCourseById(Integer id);
    
    /**
     * Get course by code
     * @param code Course code
     * @return Course response DTO
     */
    CourseResponseDTO getCourseByCode(String code);
    
    /**
     * Get all courses
     * @return List of courses
     */
    List<CourseResponseDTO> getAllCourses();
    
    /**
     * Delete course
     * @param id Course ID
     */
    void deleteCourse(Integer id);
}
