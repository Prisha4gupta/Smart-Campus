package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.TimetableEntryRequestDTO;
import com.sca.smartcampusbackend.dto.TimetableEntryResponseDTO;

import java.util.List;

/**
 * Service interface for TimetableEntry operations
 * Handles timetable resolution based on student enrollments
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
public interface TimetableEntryService {

    /**
     * Create a new timetable entry for a course offering
     * 
     * @param dto Timetable entry request DTO
     * @return Created entry response DTO
     */
    TimetableEntryResponseDTO createEntry(TimetableEntryRequestDTO dto);

    /**
     * Update existing timetable entry
     * 
     * @param id  Entry ID
     * @param dto Timetable entry request DTO
     * @return Updated entry response DTO
     */
    TimetableEntryResponseDTO updateEntry(Long id, TimetableEntryRequestDTO dto);

    /**
     * Get timetable entry by ID
     * 
     * @param id Entry ID
     * @return Entry response DTO
     */
    TimetableEntryResponseDTO getEntryById(Long id);

    /**
     * Get all timetable entries for an offering
     * 
     * @param offeringId Offering ID
     * @return List of entries
     */
    List<TimetableEntryResponseDTO> getEntriesByOffering(Long offeringId);

    /**
     * Get personalized timetable for a student based on their enrollments
     * This is the KEY METHOD for the enrollment-based timetable system
     * 
     * @param userId Student user ID
     * @return List of timetable entries for all enrolled courses
     */
    List<TimetableEntryResponseDTO> getPersonalizedTimetable(Long userId);

    /**
     * Get personalized timetable for a specific day
     * 
     * @param userId    Student user ID
     * @param dayOfWeek Day of week (1-7)
     * @return List of entries for that day
     */
    List<TimetableEntryResponseDTO> getPersonalizedTimetableByDay(Long userId, Integer dayOfWeek);

    /**
     * Get all timetable entries (admin)
     * 
     * @return List of all entries with course/faculty details
     */
    List<TimetableEntryResponseDTO> getAllEntries();

    /**
     * Delete timetable entry
     * 
     * @param id Entry ID
     */
    void deleteEntry(Long id);
}
