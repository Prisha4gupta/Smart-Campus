package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.FacultyRequestDTO;
import com.sca.smartcampusbackend.dto.FacultyResponseDTO;
import com.sca.smartcampusbackend.entity.Faculty;
import com.sca.smartcampusbackend.repository.FacultyRepository;
import com.sca.smartcampusbackend.service.FacultyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FacultyService
 * Handles all business logic for faculty operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class FacultyServiceImpl implements FacultyService {
    
    private final FacultyRepository facultyRepository;
    
    @Override
    @Transactional
    public FacultyResponseDTO createFaculty(FacultyRequestDTO dto) {
        // Check if email already exists
        if (facultyRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Faculty with email " + dto.getEmail() + " already exists");
        }
        
        Faculty faculty = new Faculty();
        faculty.setFirstName(dto.getFirstName());
        faculty.setLastName(dto.getLastName());
        faculty.setEmail(dto.getEmail());
        faculty.setDepartment(dto.getDepartment());
        faculty.setPhone(dto.getPhone());
        faculty.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        Faculty saved = facultyRepository.save(faculty);
        return toResponseDTO(saved);
    }
    
    @Override
    @Transactional
    public FacultyResponseDTO updateFaculty(Long id, FacultyRequestDTO dto) {
        Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with id: " + id));
        
        // Check if email is being changed and if new email already exists
        if (!faculty.getEmail().equals(dto.getEmail())) {
            if (facultyRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Faculty with email " + dto.getEmail() + " already exists");
            }
        }
        
        faculty.setFirstName(dto.getFirstName());
        faculty.setLastName(dto.getLastName());
        faculty.setEmail(dto.getEmail());
        faculty.setDepartment(dto.getDepartment());
        faculty.setPhone(dto.getPhone());
        faculty.setIsActive(dto.getIsActive());
        
        Faculty updated = facultyRepository.save(faculty);
        return toResponseDTO(updated);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FacultyResponseDTO getFacultyById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with id: " + id));
        return toResponseDTO(faculty);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDTO> getAllFaculty() {
        return facultyRepository.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDTO> getFacultyByDepartment(String department) {
        return facultyRepository.findByDepartment(department).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FacultyResponseDTO> getActiveFaculty() {
        return facultyRepository.findByIsActive(true).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with id: " + id));
        
        // Soft delete
        faculty.setIsActive(false);
        facultyRepository.save(faculty);
    }
    
    /**
     * Convert Faculty entity to response DTO
     */
    private FacultyResponseDTO toResponseDTO(Faculty faculty) {
        return new FacultyResponseDTO(
            faculty.getId(),
            faculty.getFirstName(),
            faculty.getLastName(),
            faculty.getEmail(),
            faculty.getDepartment(),
            faculty.getPhone(),
            faculty.getIsActive(),
            faculty.getCreatedAt(),
            faculty.getUpdatedAt()
        );
    }
}
