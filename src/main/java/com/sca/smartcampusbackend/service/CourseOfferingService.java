package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.CourseOfferingRequestDTO;
import com.sca.smartcampusbackend.dto.CourseOfferingResponseDTO;

import java.util.List;

/**
 * Service interface for Course Offering operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
public interface CourseOfferingService {
    
    /**
     * Create a new course offering
     * @param dto Course offering request DTO
     * @return Created offering response DTO
     */
    CourseOfferingResponseDTO createOffering(CourseOfferingRequestDTO dto);
    
    /**
     * Update existing course offering
     * @param id Offering ID
     * @param dto Course offering request DTO
     * @return Updated offering response DTO
     */
    CourseOfferingResponseDTO updateOffering(Long id, CourseOfferingRequestDTO dto);
    
    /**
     * Get offering by ID
     * @param id Offering ID
     * @return Offering response DTO
     */
    CourseOfferingResponseDTO getOfferingById(Long id);
    
    /**
     * Get all offerings
     * @return List of offerings
     */
    List<CourseOfferingResponseDTO> getAllOfferings();
    
    /**
     * Get offerings by semester
     * @param semester Semester identifier
     * @return List of offerings
     */
    List<CourseOfferingResponseDTO> getOfferingsBySemester(String semester);
    
    /**
     * Get offerings by faculty
     * @param facultyId Faculty ID
     * @return List of offerings
     */
    List<CourseOfferingResponseDTO> getOfferingsByFaculty(Long facultyId);
    
    /**
     * Delete offering
     * @param id Offering ID
     */
    void deleteOffering(Long id);
}
