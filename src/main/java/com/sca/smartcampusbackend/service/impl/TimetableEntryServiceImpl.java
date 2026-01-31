package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.TimetableEntryRequestDTO;
import com.sca.smartcampusbackend.dto.TimetableEntryResponseDTO;
import com.sca.smartcampusbackend.entity.CourseOffering;
import com.sca.smartcampusbackend.entity.TimetableEntry;
import com.sca.smartcampusbackend.repository.CourseOfferingRepository;
import com.sca.smartcampusbackend.repository.TimetableEntryRepository;
import com.sca.smartcampusbackend.service.TimetableEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TimetableEntryService
 * Resolves student timetables from enrollments + timetable entries
 * 
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class TimetableEntryServiceImpl implements TimetableEntryService {

    private final TimetableEntryRepository entryRepository;
    private final CourseOfferingRepository offeringRepository;

    private static final String[] DAY_NAMES = {
            "", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    @Override
    @Transactional
    public TimetableEntryResponseDTO createEntry(TimetableEntryRequestDTO dto) {
        CourseOffering offering = offeringRepository.findById(dto.getOfferingId())
                .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + dto.getOfferingId()));

        TimetableEntry entry = new TimetableEntry();
        entry.setOffering(offering);
        entry.setDayOfWeek(dto.getDayOfWeek());
        entry.setStartTime(dto.getStartTime());
        entry.setEndTime(dto.getEndTime());
        entry.setRoom(dto.getRoom());

        TimetableEntry saved = entryRepository.save(entry);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public TimetableEntryResponseDTO updateEntry(Long id, TimetableEntryRequestDTO dto) {
        TimetableEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with id: " + id));

        CourseOffering offering = offeringRepository.findById(dto.getOfferingId())
                .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + dto.getOfferingId()));

        entry.setOffering(offering);
        entry.setDayOfWeek(dto.getDayOfWeek());
        entry.setStartTime(dto.getStartTime());
        entry.setEndTime(dto.getEndTime());
        entry.setRoom(dto.getRoom());

        TimetableEntry updated = entryRepository.save(entry);
        return toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public TimetableEntryResponseDTO getEntryById(Long id) {
        TimetableEntry entry = entryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found with id: " + id));
        return toResponseDTO(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponseDTO> getEntriesByOffering(Long offeringId) {
        return entryRepository.findByOffering_Id(offeringId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponseDTO> getPersonalizedTimetable(Long userId) {
        // This query joins:
        // 1. student_enrollments (where user_id = userId AND status = ENROLLED)
        // 2. course_offerings (via enrollment.offering_id)
        // 3. timetable_entries (via offering.id)
        // Result: All meeting times for courses the student is enrolled in

        return entryRepository.findPersonalizedTimetable(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponseDTO> getPersonalizedTimetableByDay(Long userId, Integer dayOfWeek) {
        return entryRepository.findPersonalizedTimetableByDay(userId, dayOfWeek).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableEntryResponseDTO> getAllEntries() {
        return entryRepository.findAllWithDetails().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteEntry(Long id) {
        if (!entryRepository.existsById(id)) {
            throw new IllegalArgumentException("Entry not found with id: " + id);
        }
        entryRepository.deleteById(id);
    }

    /**
     * Convert TimetableEntry entity to response DTO
     */
    private TimetableEntryResponseDTO toResponseDTO(TimetableEntry entry) {
        CourseOffering offering = entry.getOffering();

        String facultyName = offering.getFaculty().getFirstName() + " " +
                offering.getFaculty().getLastName();

        return new TimetableEntryResponseDTO(
                entry.getId(),
                offering.getId(),
                offering.getCourse().getCode(),
                offering.getCourse().getTitle(),
                facultyName,
                entry.getDayOfWeek(),
                DAY_NAMES[entry.getDayOfWeek()],
                entry.getStartTime(),
                entry.getEndTime(),
                entry.getRoom());
    }
}
