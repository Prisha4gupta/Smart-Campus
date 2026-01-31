package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.DashboardResponse;
import com.sca.smartcampusbackend.dto.DeadlineDTO;
import com.sca.smartcampusbackend.dto.EventDTO;
import com.sca.smartcampusbackend.dto.NextClassDTO;
import com.sca.smartcampusbackend.dto.SystemStatsDTO;
import com.sca.smartcampusbackend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation for dashboard aggregation.
 *
 * Notes:
 * - Uses raw SQL via JdbcTemplate so this feature does not require JPA entity classes to exist at compile time.
 * - Queries assume the following table names and columns exist:
 *   - timetable_entries(offering_id, day_of_week int (1=Sunday..7=Saturday), start_time TIME, end_time TIME, room VARCHAR)
 *   - student_enrollments(user_id, offering_id, status)
 *   - course_offerings(id, course_id, faculty_id)
 *   - courses(id, code, title)
 *   - faculty(id, first_name, last_name)
 *   - deadlines(id, student_id, title, due_date DATE, course)
 *   - events(id, title, body TEXT, start_datetime DATETIME, is_public BOOLEAN)
 *
 * If your real schema differs, adjust the SQL strings below.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DashboardServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DashboardResponse getDashboardForStudent(Long studentId) {
        NextClassDTO nextClass = fetchNextClass(studentId);
        List<DeadlineDTO> deadlines = fetchUpcomingDeadlines(studentId);
        List<EventDTO> events = fetchUpcomingEvents();
        List<String> recommendations = buildRecommendations(nextClass, deadlines, events);

        return new DashboardResponse(nextClass, deadlines, events, recommendations);
    }

    private NextClassDTO fetchNextClass(Long studentId) {
        try {
            // use IST timezone (Asia/Kolkata)
            ZoneId ist = ZoneId.of("Asia/Kolkata");
            LocalTime nowTime = LocalTime.now(Clock.system(ist));

            // compute current day-of-week number MySQL's DAYOFWEEK returns 1=Sunday..7=Saturday
            Integer dow = jdbcTemplate.queryForObject("SELECT DAYOFWEEK(CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata'))", Integer.class);

            // Query the new timetable_entries table with enrollments
            String sql = "SELECT c.code as subject, te.room, CONCAT(f.first_name, ' ', f.last_name) as faculty_name, " +
                    "te.start_time, te.end_time " +
                    "FROM timetable_entries te " +
                    "INNER JOIN course_offerings co ON te.offering_id = co.id " +
                    "INNER JOIN student_enrollments se ON se.offering_id = co.id " +
                    "INNER JOIN courses c ON co.course_id = c.id " +
                    "INNER JOIN faculty f ON co.faculty_id = f.id " +
                    "WHERE se.user_id = ? AND se.status = 'ENROLLED' " +
                    "AND te.day_of_week = ? AND te.start_time > TIME(CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata')) " +
                    "ORDER BY te.start_time LIMIT 1";

            return jdbcTemplate.queryForObject(sql, new Object[]{studentId, dow}, new RowMapper<NextClassDTO>() {
                @Override
                public NextClassDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String subject = rs.getString("subject");
                    String room = rs.getString("room");
                    String facultyName = rs.getString("faculty_name");
                    LocalTime st = rs.getTime("start_time").toLocalTime();
                    LocalTime et = rs.getTime("end_time").toLocalTime();
                    return new NextClassDTO(subject, room, facultyName, st, et);
                }
            });
        } catch (Exception e) {
            // If no result or table doesn't exist, return null (no next class found).
            return null;
        }
    }

    private List<DeadlineDTO> fetchUpcomingDeadlines(Long studentId) {
        try {
            String sql = "SELECT id, title, due_date, course FROM deadlines " +
                    "WHERE student_id = ? AND due_date >= CONVERT_TZ(CURDATE(),'SYSTEM','Asia/Kolkata') " +
                    "ORDER BY due_date LIMIT 5";

            return jdbcTemplate.query(sql, new Object[]{studentId}, (rs, rowNum) -> {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                LocalDate due = rs.getDate("due_date").toLocalDate();
                String course = rs.getString("course");
                return new DeadlineDTO(id, title, due, course);
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<EventDTO> getUpcomingEvents() {
        return fetchUpcomingEvents();
    }
    
    @Override
    public SystemStatsDTO getSystemStats() {
        SystemStatsDTO stats = new SystemStatsDTO();
        
        try {
            // Count total courses
            Long totalCourses = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM courses", Long.class);
            stats.setTotalCourses(totalCourses != null ? totalCourses : 0L);
        } catch (Exception e) {
            stats.setTotalCourses(0L);
        }
        
        try {
            // Count total students (users with STUDENT or ROLE_STUDENT)
            Long totalStudents = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role IN ('STUDENT', 'ROLE_STUDENT')", Long.class);
            stats.setTotalStudents(totalStudents != null ? totalStudents : 0L);
        } catch (Exception e) {
            stats.setTotalStudents(0L);
        }
        
        try {
            // Count active faculty members
            Long totalFaculty = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM faculty WHERE is_active = TRUE", Long.class);
            stats.setTotalFaculty(totalFaculty != null ? totalFaculty : 0L);
        } catch (Exception e) {
            stats.setTotalFaculty(0L);
        }
        
        try {
            // Count active/upcoming events (within next 30 days)
            Long activeEvents = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM events " +
                "WHERE is_public = 1 " +
                "AND start_datetime >= CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata') " +
                "AND start_datetime <= DATE_ADD(CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata'), INTERVAL 30 DAY)",
                Long.class);
            stats.setActiveEvents(activeEvents != null ? activeEvents : 0L);
        } catch (Exception e) {
            stats.setActiveEvents(0L);
        }
        
        return stats;
    }
    
    private List<EventDTO> fetchUpcomingEvents() {
        try {
            String sql = "SELECT id, title, body as description, DATE(start_datetime) as date, '' as location FROM events " +
                    "WHERE is_public = 1 " +
                    "AND start_datetime >= CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata') " +
                    "AND start_datetime <= DATE_ADD(CONVERT_TZ(NOW(),'SYSTEM','Asia/Kolkata'), INTERVAL 30 DAY) " +
                    "ORDER BY start_datetime LIMIT 10";

            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Long id = rs.getLong("id");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                LocalDate date = rs.getDate("date").toLocalDate();
                String loc = rs.getString("location");
                return new EventDTO(id, title, desc, date, loc);
            });
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private List<String> buildRecommendations(NextClassDTO nextClass, List<DeadlineDTO> deadlines, List<EventDTO> events) {
        List<String> recs = new ArrayList<>();
        if (nextClass != null) {
            recs.add("Prepare for your next class: " + nextClass.getSubject() + " at " + nextClass.getStartTime());
        }
        if (!deadlines.isEmpty()) {
            recs.add("You have " + deadlines.size() + " upcoming deadlines — plan your week!");
        }
        if (!events.isEmpty()) {
            recs.add("Events are happening soon on campus — check them out!");
        }
        if (recs.isEmpty()) recs.add("No immediate notifications — good job!");
        return recs;
    }
}
