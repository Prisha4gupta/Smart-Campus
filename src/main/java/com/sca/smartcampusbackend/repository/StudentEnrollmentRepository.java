package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.StudentEnrollment;
import com.sca.smartcampusbackend.entity.StudentEnrollment.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long> {

    List<StudentEnrollment> findByUser_Id(Long userId);

    List<StudentEnrollment> findByOffering_Id(Long offeringId);

    List<StudentEnrollment> findByUser_IdAndStatus(Long userId, EnrollmentStatus status);

    List<StudentEnrollment> findByOffering_IdAndStatus(Long offeringId, EnrollmentStatus status);

    Optional<StudentEnrollment> findByUser_IdAndOffering_Id(Long userId, Long offeringId);

    long countByOffering_IdAndStatus(Long offeringId, EnrollmentStatus status);

    @Query("SELECT se FROM StudentEnrollment se " +
            "WHERE se.user.id = :userId " +
            "AND se.offering.semester = :semester " +
            "AND se.status = 'ENROLLED'")
    List<StudentEnrollment> findActiveEnrollmentsBySemester(
            @Param("userId") Long userId,
            @Param("semester") String semester);

    @Query("SELECT se FROM StudentEnrollment se " +
            "LEFT JOIN FETCH se.user " +
            "WHERE se.offering.id = :offeringId " +
            "AND se.status = 'ENROLLED'")
    List<StudentEnrollment> findEnrolledStudentsByOfferingIdWithUser(@Param("offeringId") Long offeringId);
}
