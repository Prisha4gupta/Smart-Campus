package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.EventRequestDTO;
import com.sca.smartcampusbackend.dto.EventResponseDTO;

import java.util.List;

/**
 * Service interface for Event operations
 * 
 * @since 1.0.0
 */
public interface EventService {
    
    /**
     * Create a new event
     * @param dto Event request DTO
     * @param createdByUserId User ID creating the event
     * @return Created event response DTO
     */
    EventResponseDTO createEvent(EventRequestDTO dto, Long createdByUserId);
    
    /**
     * Update existing event
     * @param id Event ID
     * @param dto Event request DTO
     * @return Updated event response DTO
     */
    EventResponseDTO updateEvent(Long id, EventRequestDTO dto);
    
    /**
     * Get event by ID
     * @param id Event ID
     * @return Event response DTO
     */
    EventResponseDTO getEventById(Long id);
    
    /**
     * Get all events
     * @return List of events
     */
    List<EventResponseDTO> getAllEvents();
    
    /**
     * Get all public events
     * @return List of public events
     */
    List<EventResponseDTO> getPublicEvents();
    
    /**
     * Get upcoming public events
     * @return List of upcoming events
     */
    List<EventResponseDTO> getUpcomingEvents();
    
    /**
     * Get events for dashboard (next 7 days)
     * @return List of events
     */
    List<EventResponseDTO> getEventsForDashboard();
    
    /**
     * Delete event
     * @param id Event ID
     */
    void deleteEvent(Long id);
}
