package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.CourseOfferingRequestDTO;
import com.sca.smartcampusbackend.dto.CourseOfferingResponseDTO;
import com.sca.smartcampusbackend.service.CourseOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Course Offering operations
 * Provides endpoints for managing course sections and electives
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/offerings")
@RequiredArgsConstructor
public class CourseOfferingController {
    
    private final CourseOfferingService offeringService;
    
    /**
     * Create new course offering (Admin only)
     * POST /api/offerings
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseOfferingResponseDTO> createOffering(@Valid @RequestBody CourseOfferingRequestDTO dto) {
        CourseOfferingResponseDTO created = offeringService.createOffering(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Update course offering (Admin only)
     * PUT /api/offerings/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseOfferingResponseDTO> updateOffering(
            @PathVariable Long id,
            @Valid @RequestBody CourseOfferingRequestDTO dto) {
        CourseOfferingResponseDTO updated = offeringService.updateOffering(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get offering by ID (All authenticated users)
     * GET /api/offerings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseOfferingResponseDTO> getOfferingById(@PathVariable Long id) {
        CourseOfferingResponseDTO offering = offeringService.getOfferingById(id);
        return ResponseEntity.ok(offering);
    }
    
    /**
     * Get all offerings (All authenticated users)
     * GET /api/offerings
     */
    @GetMapping
    public ResponseEntity<List<CourseOfferingResponseDTO>> getAllOfferings() {
        List<CourseOfferingResponseDTO> offerings = offeringService.getAllOfferings();
        return ResponseEntity.ok(offerings);
    }
    
    /**
     * Get offerings by semester (All authenticated users)
     * GET /api/offerings/semester/{semester}
     */
    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<CourseOfferingResponseDTO>> getOfferingsBySemester(@PathVariable String semester) {
        List<CourseOfferingResponseDTO> offerings = offeringService.getOfferingsBySemester(semester);
        return ResponseEntity.ok(offerings);
    }
    
    /**
     * Get offerings by faculty (All authenticated users)
     * GET /api/offerings/faculty/{facultyId}
     */
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<List<CourseOfferingResponseDTO>> getOfferingsByFaculty(@PathVariable Long facultyId) {
        List<CourseOfferingResponseDTO> offerings = offeringService.getOfferingsByFaculty(facultyId);
        return ResponseEntity.ok(offerings);
    }
    
    /**
     * Delete offering (Admin only)
     * DELETE /api/offerings/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOffering(@PathVariable Long id) {
        offeringService.deleteOffering(id);
        return ResponseEntity.noContent().build();
    }
}
