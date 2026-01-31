package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for TimetableEntry entity
 * Provides database access methods for timetable entry operations
 * 
 * @since 1.0.0
 */
@Repository
public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    /**
     * Find all timetable entries for a specific offering
     * 
     * @param offeringId Course offering ID
     * @return List of timetable entries
     */
    List<TimetableEntry> findByOffering_Id(Long offeringId);

    /**
     * Find timetable entries by offering and day of week
     * 
     * @param offeringId Offering ID
     * @param dayOfWeek  Day of week (1-7)
     * @return List of entries
     */
    List<TimetableEntry> findByOffering_IdAndDayOfWeek(Long offeringId, Integer dayOfWeek);

    /**
     * Find personalized timetable for a student based on their enrollments
     * 
     * @param userId User ID
     * @return List of timetable entries
     */
    @Query("SELECT te FROM TimetableEntry te " +
            "JOIN te.offering co " +
            "JOIN StudentEnrollment se ON se.offering.id = co.id " +
            "WHERE se.user.id = :userId " +
            "AND se.status = 'ENROLLED' " +
            "ORDER BY te.dayOfWeek, te.startTime")
    List<TimetableEntry> findPersonalizedTimetable(@Param("userId") Long userId);

    /**
     * Find timetable for a student on a specific day
     * 
     * @param userId    User ID
     * @param dayOfWeek Day of week (1-7)
     * @return List of timetable entries for that day
     */
    @Query("SELECT te FROM TimetableEntry te " +
            "JOIN te.offering co " +
            "JOIN StudentEnrollment se ON se.offering.id = co.id " +
            "WHERE se.user.id = :userId " +
            "AND se.status = 'ENROLLED' " +
            "AND te.dayOfWeek = :dayOfWeek " +
            "ORDER BY te.startTime")
    List<TimetableEntry> findPersonalizedTimetableByDay(
            @Param("userId") Long userId,
            @Param("dayOfWeek") Integer dayOfWeek);

    /**
     * Find all timetable entries with offering and course eagerly loaded
     * Prevents LazyInitializationException
     */
    @Query("SELECT te FROM TimetableEntry te " +
            "JOIN FETCH te.offering o " +
            "JOIN FETCH o.course " +
            "JOIN FETCH o.faculty " +
            "ORDER BY te.dayOfWeek, te.startTime")
    List<TimetableEntry> findAllWithDetails();
}
