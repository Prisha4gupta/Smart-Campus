package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {

    List<CourseOffering> findBySemester(String semester);

    List<CourseOffering> findByCourse_Id(Integer courseId);

    List<CourseOffering> findByFaculty_Id(Long facultyId);

    @Query("SELECT co FROM CourseOffering co WHERE co.course.id = :courseId " +
            "AND co.semester = :semester AND co.sectionCode = :sectionCode")
    Optional<CourseOffering> findByCourseAndSemesterAndSection(
            @Param("courseId") Integer courseId,
            @Param("semester") String semester,
            @Param("sectionCode") String sectionCode);

    List<CourseOffering> findBySemesterAndFaculty_Id(String semester, Long facultyId);

    @Query("SELECT co FROM CourseOffering co " +
            "LEFT JOIN FETCH co.course " +
            "LEFT JOIN FETCH co.faculty")
    List<CourseOffering> findAllWithCourseAndFaculty();
}
