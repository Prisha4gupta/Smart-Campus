package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.CourseRequestDTO;
import com.sca.smartcampusbackend.dto.CourseResponseDTO;
import com.sca.smartcampusbackend.entity.Course;
import com.sca.smartcampusbackend.repository.CourseRepository;
import com.sca.smartcampusbackend.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CourseService
 * Handles all business logic for course operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    
    private final CourseRepository courseRepository;
    
    @Override
    @Transactional
    public CourseResponseDTO createCourse(CourseRequestDTO dto) {
        // Check if course code already exists
        if (courseRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Course with code " + dto.getCode() + " already exists");
        }
        
        Course course = new Course();
        course.setCode(dto.getCode());
        course.setTitle(dto.getTitle());
        course.setCredits(dto.getCredits());
        course.setDescription(dto.getDescription());
        
        Course saved = courseRepository.save(course);
        return toResponseDTO(saved);
    }
    
    @Override
    @Transactional
    public CourseResponseDTO updateCourse(Integer id, CourseRequestDTO dto) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
        
        // Check if code is being changed and if new code already exists
        if (!course.getCode().equals(dto.getCode())) {
            if (courseRepository.existsByCode(dto.getCode())) {
                throw new IllegalArgumentException("Course with code " + dto.getCode() + " already exists");
            }
        }
        
        course.setCode(dto.getCode());
        course.setTitle(dto.getTitle());
        course.setCredits(dto.getCredits());
        course.setDescription(dto.getDescription());
        
        Course updated = courseRepository.save(course);
        return toResponseDTO(updated);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Integer id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + id));
        return toResponseDTO(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseByCode(String code) {
        Course course = courseRepository.findByCode(code)
            .orElseThrow(() -> new IllegalArgumentException("Course not found with code: " + code));
        return toResponseDTO(course);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteCourse(Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new IllegalArgumentException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
    
    /**
     * Convert Course entity to response DTO
     */
    private CourseResponseDTO toResponseDTO(Course course) {
        return new CourseResponseDTO(
            course.getId(),
            course.getCode(),
            course.getTitle(),
            course.getCredits(),
            course.getDescription(),
            course.getCreatedAt()
        );
    }
}
