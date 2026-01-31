package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.CourseRequestDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Course operations
 * 
 * @since 1.0.0
 */
public interface CourseService {

    /**
     * Create a new course
     * 
     * @param dto Course request DTO
     * @return Created course response DTO
     */
    CourseResponseDTO createCourse(CourseRequestDTO dto);

    /**
     * Update existing course
     * 
     * @param id  Course ID
     * @param dto Course request DTO
     * @return Updated course response DTO
     */
    CourseResponseDTO updateCourse(Integer id, CourseRequestDTO dto);

    /**
     * Get course by ID
     * 
     * @param id Course ID
     * @return Course response DTO
     */
    CourseResponseDTO getCourseById(Integer id);

    /**
     * Get course by code
     * 
     * @param code Course code
     * @return Course response DTO
     */
    CourseResponseDTO getCourseByCode(String code);

    /**
     * Get all courses
     * 
     * @return List of courses
     */
    List<CourseResponseDTO> getAllCourses();

    /**
     * Get all courses with pagination and sorting
     * 
     * @param pageable Pagination and sorting information
     * @return Page of courses
     */
    Page<CourseResponseDTO> getAllCourses(Pageable pageable);

    /**
     * Search courses by title
     * 
     * @param title    Search query
     * @param pageable Pagination information
     * @return Page of matching courses
     */
    Page<CourseResponseDTO> searchCoursesByTitle(String title, Pageable pageable);

    /**
     * Delete course
     * 
     * @param id Course ID
     */
    void deleteCourse(Integer id);
}
