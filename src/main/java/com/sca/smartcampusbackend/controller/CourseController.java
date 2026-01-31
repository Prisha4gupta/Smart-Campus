package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.CourseRequestDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;
import com.sca.smartcampusbackend.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Course operations
 * Provides endpoints for managing course catalog
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    
    /**
     * Create new course (Admin only)
     * POST /api/courses
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO created = courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Update course (Admin only)
     * PUT /api/courses/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Integer id,
            @Valid @RequestBody CourseRequestDTO dto) {
        CourseResponseDTO updated = courseService.updateCourse(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Get course by ID (All authenticated users)
     * GET /api/courses/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Integer id) {
        CourseResponseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }
    
    /**
     * Get course by code (All authenticated users)
     * GET /api/courses/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<CourseResponseDTO> getCourseByCode(@PathVariable String code) {
        CourseResponseDTO course = courseService.getCourseByCode(code);
        return ResponseEntity.ok(course);
    }
    
    /**
     * Get all courses (All authenticated users)
     * GET /api/courses
     */
    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }
    
    /**
     * Delete course (Admin only)
     * DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
