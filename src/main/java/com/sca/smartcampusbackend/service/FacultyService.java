package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.FacultyRequestDTO;
import com.sca.smartcampusbackend.dto.FacultyResponseDTO;

import java.util.List;

/**
 * Service interface for Faculty operations
 * 
 * @since 1.0.0
 */
public interface FacultyService {
    
    /**
     * Create a new faculty member
     * @param dto Faculty request DTO
     * @return Created faculty response DTO
     */
    FacultyResponseDTO createFaculty(FacultyRequestDTO dto);
    
    /**
     * Update existing faculty
     * @param id Faculty ID
     * @param dto Faculty request DTO
     * @return Updated faculty response DTO
     */
    FacultyResponseDTO updateFaculty(Long id, FacultyRequestDTO dto);
    
    /**
     * Get faculty by ID
     * @param id Faculty ID
     * @return Faculty response DTO
     */
    FacultyResponseDTO getFacultyById(Long id);
    
    /**
     * Get all faculty
     * @return List of faculty
     */
    List<FacultyResponseDTO> getAllFaculty();
    
    /**
     * Get faculty by department
     * @param department Department name
     * @return List of faculty in the department
     */
    List<FacultyResponseDTO> getFacultyByDepartment(String department);
    
    /**
     * Get all active faculty
     * @return List of active faculty
     */
    List<FacultyResponseDTO> getActiveFaculty();
    
    /**
     * Delete faculty (soft delete by setting isActive = false)
     * @param id Faculty ID
     */
    void deleteFaculty(Long id);
}
