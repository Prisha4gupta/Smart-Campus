package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.TimetableEntryRequestDTO;
import com.sca.smartcampusbackend.dto.TimetableEntryResponseDTO;
import com.sca.smartcampusbackend.service.TimetableEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for TimetableEntry operations
 * Provides endpoints for managing timetable entries and personalized timetables
 * 
 * This is the NEW enrollment-based timetable system that replaces the old
 * student_id-based timetable table.
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/timetable-entries")
@RequiredArgsConstructor
public class TimetableEntryController {

    private final TimetableEntryService timetableEntryService;

    /**
     * Create new timetable entry (Admin only)
     * POST /api/timetable-entries
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableEntryResponseDTO> createEntry(@Valid @RequestBody TimetableEntryRequestDTO dto) {
        TimetableEntryResponseDTO created = timetableEntryService.createEntry(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update timetable entry (Admin only)
     * PUT /api/timetable-entries/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TimetableEntryResponseDTO> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody TimetableEntryRequestDTO dto) {
        TimetableEntryResponseDTO updated = timetableEntryService.updateEntry(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get all timetable entries (Admin)
     * GET /api/timetable-entries
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TimetableEntryResponseDTO>> getAllEntries() {
        List<TimetableEntryResponseDTO> entries = timetableEntryService.getAllEntries();
        return ResponseEntity.ok(entries);
    }

    /**
     * Get timetable entry by ID (All authenticated users)
     * GET /api/timetable-entries/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TimetableEntryResponseDTO> getEntryById(@PathVariable Long id) {
        TimetableEntryResponseDTO entry = timetableEntryService.getEntryById(id);
        return ResponseEntity.ok(entry);
    }

    /**
     * Get all timetable entries for an offering (All authenticated users)
     * GET /api/timetable-entries/offering/{offeringId}
     */
    @GetMapping("/offering/{offeringId}")
    public ResponseEntity<List<TimetableEntryResponseDTO>> getEntriesByOffering(@PathVariable Long offeringId) {
        List<TimetableEntryResponseDTO> entries = timetableEntryService.getEntriesByOffering(offeringId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get personalized timetable for a student (KEY ENDPOINT)
     * GET /api/timetable-entries/student/{userId}
     * 
     * Returns all meeting times for courses the student is enrolled in.
     * This derives the timetable from:
     * 1. student_enrollments (what courses they're taking)
     * 2. course_offerings (which section/instructor)
     * 3. timetable_entries (when/where it meets)
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<List<TimetableEntryResponseDTO>> getPersonalizedTimetable(@PathVariable Long userId) {
        List<TimetableEntryResponseDTO> timetable = timetableEntryService.getPersonalizedTimetable(userId);
        return ResponseEntity.ok(timetable);
    }

    /**
     * Get personalized timetable for a specific day
     * GET /api/timetable-entries/student/{userId}/day/{dayOfWeek}
     */
    @GetMapping("/student/{userId}/day/{dayOfWeek}")
    public ResponseEntity<List<TimetableEntryResponseDTO>> getPersonalizedTimetableByDay(
            @PathVariable Long userId,
            @PathVariable Integer dayOfWeek) {
        List<TimetableEntryResponseDTO> timetable = timetableEntryService.getPersonalizedTimetableByDay(userId,
                dayOfWeek);
        return ResponseEntity.ok(timetable);
    }

    /**
     * Delete timetable entry (Admin only)
     * DELETE /api/timetable-entries/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long id) {
        timetableEntryService.deleteEntry(id);
        return ResponseEntity.noContent().build();
    }
}
