package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Event entity
 * Provides database access methods for event operations
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Find all public events
     * 
     * @return List of public events
     */
    List<Event> findByIsPublicTrue();

    /**
     * Find events created by a specific user
     * 
     * @param userId User ID
     * @return List of events
     */
    List<Event> findByCreatedBy_Id(Long userId);

    /**
     * Find upcoming public events (starting after current time)
     * 
     * @param now Current datetime
     * @return List of upcoming events
     */
    @Query("SELECT e FROM Event e " +
            "WHERE e.isPublic = true " +
            "AND e.startDatetime >= :now " +
            "ORDER BY e.startDatetime")
    List<Event> findUpcomingPublicEvents(@Param("now") LocalDateTime now);

    /**
     * Find events within a date range
     * 
     * @param start Start datetime
     * @param end   End datetime
     * @return List of events
     */
    @Query("SELECT e FROM Event e " +
            "WHERE e.startDatetime BETWEEN :start AND :end " +
            "ORDER BY e.startDatetime")
    List<Event> findEventsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * Find upcoming events for dashboard (next 7 days, public only)
     * 
     * @param now            Current datetime
     * @param sevenDaysLater 7 days from now
     * @return List of events
     */
    @Query("SELECT e FROM Event e " +
            "WHERE e.isPublic = true " +
            "AND e.startDatetime BETWEEN :now AND :sevenDaysLater " +
            "ORDER BY e.startDatetime")
    List<Event> findUpcomingEventsForDashboard(
            @Param("now") LocalDateTime now,
            @Param("sevenDaysLater") LocalDateTime sevenDaysLater);

    /**
     * Find all events with createdBy eagerly loaded
     */
    @Query("SELECT e FROM Event e JOIN FETCH e.createdBy ORDER BY e.startDatetime DESC")
    List<Event> findAllWithCreator();
}
