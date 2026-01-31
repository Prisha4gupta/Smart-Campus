package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.FacultyRequestDTO;
import com.sca.smartcampusbackend.dto.FacultyResponseDTO;
import com.sca.smartcampusbackend.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Faculty operations
 * Provides endpoints for managing faculty directory
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/faculty")
@RequiredArgsConstructor
public class FacultyController {
    
    private final FacultyService facultyService;
    
    /**
     * Create new faculty (Admin only)
     * POST /api/faculty
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponseDTO> createFaculty(@Valid @RequestBody FacultyRequestDTO dto) {
        FacultyResponseDTO created = facultyService.createFaculty(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Update faculty (Admin only)
     * PUT /api/faculty/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultyResponseDTO> updateFaculty(
            @PathVariable Long id,
            @Valid @RequestBody FacultyRequestDTO dto) {
        FacultyResponseDTO updated = facultyService.updateFaculty(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get faculty by ID (All authenticated users)
     * GET /api/faculty/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponseDTO> getFacultyById(@PathVariable Long id) {
        FacultyResponseDTO faculty = facultyService.getFacultyById(id);
        return ResponseEntity.ok(faculty);
    }
    
    /**
     * Get all faculty (All authenticated users)
     * GET /api/faculty
     */
    @GetMapping
    public ResponseEntity<List<FacultyResponseDTO>> getAllFaculty() {
        List<FacultyResponseDTO> faculty = facultyService.getAllFaculty();
        return ResponseEntity.ok(faculty);
    }
    
    /**
     * Get faculty by department (All authenticated users)
     * GET /api/faculty/department/{department}
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<FacultyResponseDTO>> getFacultyByDepartment(@PathVariable String department) {
        List<FacultyResponseDTO> faculty = facultyService.getFacultyByDepartment(department);
        return ResponseEntity.ok(faculty);
    }
    
    /**
     * Get active faculty (All authenticated users)
     * GET /api/faculty/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<FacultyResponseDTO>> getActiveFaculty() {
        List<FacultyResponseDTO> faculty = facultyService.getActiveFaculty();
        return ResponseEntity.ok(faculty);
    }
    
    /**
     * Delete faculty (Admin only)
     * DELETE /api/faculty/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFaculty(@PathVariable Long id) {
        facultyService.deleteFaculty(id);
        return ResponseEntity.noContent().build();
    }
}
