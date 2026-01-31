package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.EventRequestDTO;
import com.sca.smartcampusbackend.dto.EventResponseDTO;
import com.sca.smartcampusbackend.entity.Event;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.EventRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EventService
 * Handles all business logic for event operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EventResponseDTO createEvent(EventRequestDTO dto, Long createdByUserId) {
        User creator = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        Event event = new Event();
        event.setTitle(dto.getTitle());
        event.setBody(dto.getBody());
        event.setCreatedBy(creator);
        event.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : true);
        event.setStartDatetime(dto.getStartDatetime());
        event.setEndDatetime(dto.getEndDatetime());

        Event saved = eventRepository.save(event);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public EventResponseDTO updateEvent(Long id, EventRequestDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));

        event.setTitle(dto.getTitle());
        event.setBody(dto.getBody());
        event.setIsPublic(dto.getIsPublic());
        event.setStartDatetime(dto.getStartDatetime());
        event.setEndDatetime(dto.getEndDatetime());

        Event updated = eventRepository.save(event);
        return toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + id));
        return toResponseDTO(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAllWithCreator().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getPublicEvents() {
        return eventRepository.findByIsPublicTrue().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findUpcomingPublicEvents(now).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseDTO> getEventsForDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysLater = now.plusDays(7);

        return eventRepository.findUpcomingEventsForDashboard(now, sevenDaysLater).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new IllegalArgumentException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }

    /**
     * Convert Event entity to response DTO
     */
    private EventResponseDTO toResponseDTO(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getBody(),
                event.getCreatedBy().getUsername(),
                event.getIsPublic(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getCreatedAt());
    }
}
