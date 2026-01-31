package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.EventRequestDTO;
import com.sca.smartcampusbackend.dto.EventResponseDTO;
import com.sca.smartcampusbackend.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Event operations
 * Provides endpoints for managing campus events and announcements
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final com.sca.smartcampusbackend.repository.UserRepository userRepository;

    /**
     * Create new event (Admin only)
     * POST /api/events
     * 
     * CreatedBy is automatically set from authenticated user
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> createEvent(
            @Valid @RequestBody EventRequestDTO dto,
            Authentication authentication) {
        String username = ((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal())
                .getUsername();
        com.sca.smartcampusbackend.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        EventResponseDTO created = eventService.createEvent(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update event (Admin only)
     * PUT /api/events/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventRequestDTO dto) {
        EventResponseDTO updated = eventService.updateEvent(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get event by ID (All authenticated users)
     * GET /api/events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * Get all events (Admin only - includes private events)
     * GET /api/events
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Get public events (All authenticated users)
     * GET /api/events/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<EventResponseDTO>> getPublicEvents() {
        List<EventResponseDTO> events = eventService.getPublicEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Get upcoming public events (All authenticated users)
     * GET /api/events/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventResponseDTO>> getUpcomingEvents() {
        List<EventResponseDTO> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * Get events for dashboard (next 7 days, public only)
     * GET /api/events/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<List<EventResponseDTO>> getEventsForDashboard() {
        List<EventResponseDTO> events = eventService.getEventsForDashboard();
        return ResponseEntity.ok(events);
    }

    /**
     * Delete event (Admin only)
     * DELETE /api/events/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
