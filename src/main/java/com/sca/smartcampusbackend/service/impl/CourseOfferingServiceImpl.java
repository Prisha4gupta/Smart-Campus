package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.CourseOfferingRequestDTO;
import com.sca.smartcampusbackend.dto.CourseOfferingResponseDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;
import com.sca.smartcampusbackend.dto.FacultyResponseDTO;
import com.sca.smartcampusbackend.entity.Course;
import com.sca.smartcampusbackend.entity.CourseOffering;
import com.sca.smartcampusbackend.entity.Faculty;
import com.sca.smartcampusbackend.entity.StudentEnrollment.EnrollmentStatus;
import com.sca.smartcampusbackend.repository.CourseOfferingRepository;
import com.sca.smartcampusbackend.repository.CourseRepository;
import com.sca.smartcampusbackend.repository.FacultyRepository;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.service.CourseOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CourseOfferingService
 * Handles all business logic for course offering operations
 * 
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CourseOfferingServiceImpl implements CourseOfferingService {
    
    private final CourseOfferingRepository offeringRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    
    @Override
    @Transactional
    public CourseOfferingResponseDTO createOffering(CourseOfferingRequestDTO dto) {
        // Validate course exists
        Course course = courseRepository.findById(dto.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + dto.getCourseId()));
        
        // Validate faculty exists
        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with id: " + dto.getFacultyId()));
        
        // Check if offering already exists for this combination
        if (offeringRepository.findByCourseAndSemesterAndSection(
                dto.getCourseId(), dto.getSemester(), dto.getSectionCode()).isPresent()) {
            throw new IllegalArgumentException("Offering already exists for course " + course.getCode() + 
                " in semester " + dto.getSemester() + " section " + dto.getSectionCode());
        }
        
        CourseOffering offering = new CourseOffering();
        offering.setCourse(course);
        offering.setSemester(dto.getSemester());
        offering.setSectionCode(dto.getSectionCode());
        offering.setFaculty(faculty);
        offering.setCapacity(dto.getCapacity());
        
        CourseOffering saved = offeringRepository.save(offering);
        return toResponseDTO(saved);
    }
    
    @Override
    @Transactional
    public CourseOfferingResponseDTO updateOffering(Long id, CourseOfferingRequestDTO dto) {
        CourseOffering offering = offeringRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + id));
        
        // Validate course and faculty
        Course course = courseRepository.findById(dto.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + dto.getCourseId()));
        Faculty faculty = facultyRepository.findById(dto.getFacultyId())
            .orElseThrow(() -> new IllegalArgumentException("Faculty not found with id: " + dto.getFacultyId()));
        
        offering.setCourse(course);
        offering.setSemester(dto.getSemester());
        offering.setSectionCode(dto.getSectionCode());
        offering.setFaculty(faculty);
        offering.setCapacity(dto.getCapacity());
        
        CourseOffering updated = offeringRepository.save(offering);
        return toResponseDTO(updated);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseOfferingResponseDTO getOfferingById(Long id) {
        CourseOffering offering = offeringRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + id));
        return toResponseDTO(offering);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingResponseDTO> getAllOfferings() {
        return offeringRepository.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingResponseDTO> getOfferingsBySemester(String semester) {
        return offeringRepository.findBySemester(semester).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseOfferingResponseDTO> getOfferingsByFaculty(Long facultyId) {
        return offeringRepository.findByFaculty_Id(facultyId).stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteOffering(Long id) {
        if (!offeringRepository.existsById(id)) {
            throw new IllegalArgumentException("Offering not found with id: " + id);
        }
        offeringRepository.deleteById(id);
    }
    
    /**
     * Convert CourseOffering entity to response DTO with nested data
     */
    private CourseOfferingResponseDTO toResponseDTO(CourseOffering offering) {
        Course course = offering.getCourse();
        Faculty faculty = offering.getFaculty();
        
        CourseResponseDTO courseDTO = new CourseResponseDTO(
            course.getId(),
            course.getCode(),
            course.getTitle(),
            course.getCredits(),
            course.getDescription(),
            course.getCreatedAt()
        );
        
        FacultyResponseDTO facultyDTO = new FacultyResponseDTO(
            faculty.getId(),
            faculty.getFirstName(),
            faculty.getLastName(),
            faculty.getEmail(),
            faculty.getDepartment(),
            faculty.getPhone(),
            faculty.getIsActive(),
            faculty.getCreatedAt(),
            faculty.getUpdatedAt()
        );
        
        // Get enrollment count
        long enrolledCount = enrollmentRepository.countByOffering_IdAndStatus(
            offering.getId(), EnrollmentStatus.ENROLLED);
        
        return new CourseOfferingResponseDTO(
            offering.getId(),
            offering.getSemester(),
            offering.getSectionCode(),
            offering.getCapacity(),
            offering.getCreatedAt(),
            courseDTO,
            facultyDTO,
            (int) enrolledCount
        );
    }
}
