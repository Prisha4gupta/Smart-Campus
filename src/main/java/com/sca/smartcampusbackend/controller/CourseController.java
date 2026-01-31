package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.CourseRequestDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;
import com.sca.smartcampusbackend.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@Tag(name = "Courses", description = "Course management endpoints with pagination and sorting")
public class CourseController {

    private final CourseService courseService;

    /**
     * Create new course (Admin only)
     * POST /api/courses
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new course", description = "Create a new course (Admin only)")
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
    @Operation(summary = "Update a course", description = "Update an existing course (Admin only)")
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
    @Operation(summary = "Get course by ID", description = "Retrieve a specific course by its ID")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Integer id) {
        CourseResponseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    /**
     * Get course by code (All authenticated users)
     * GET /api/courses/code/{code}
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Get course by code", description = "Retrieve a course by its unique code")
    public ResponseEntity<CourseResponseDTO> getCourseByCode(@PathVariable String code) {
        CourseResponseDTO course = courseService.getCourseByCode(code);
        return ResponseEntity.ok(course);
    }

    /**
     * Get all courses (non-paginated for backward compatibility)
     * GET /api/courses
     */
    @GetMapping
    @Operation(summary = "Get all courses", description = "Retrieve all courses (non-paginated)")
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        List<CourseResponseDTO> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    /**
     * Get all courses with pagination and sorting
     * GET /api/courses/paginated?page=0&size=10&sortBy=title&sortDir=asc
     */
    @GetMapping("/paginated")
    @Operation(summary = "Get courses with pagination", description = "Retrieve courses with pagination and sorting support")
    public ResponseEntity<Page<CourseResponseDTO>> getAllCoursesPaginated(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by (e.g., title, code, credits)") @RequestParam(defaultValue = "title") String sortBy,
            @Parameter(description = "Sort direction (asc or desc)") @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CourseResponseDTO> courses = courseService.getAllCourses(pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Search courses by title with pagination
     * GET /api/courses/search?q=programming&page=0&size=10
     */
    @GetMapping("/search")
    @Operation(summary = "Search courses by title", description = "Search courses by title with pagination")
    public ResponseEntity<Page<CourseResponseDTO>> searchCourses(
            @Parameter(description = "Search query for course title") @RequestParam("q") String query,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("title").ascending());
        Page<CourseResponseDTO> courses = courseService.searchCoursesByTitle(query, pageable);
        return ResponseEntity.ok(courses);
    }

    /**
     * Delete course (Admin only)
     * DELETE /api/courses/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a course", description = "Delete a course by ID (Admin only)")
    public ResponseEntity<Void> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
