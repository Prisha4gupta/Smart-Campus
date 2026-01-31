package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.DashboardResponse;
import com.sca.smartcampusbackend.dto.EventDTO;
import com.sca.smartcampusbackend.dto.SystemStatsDTO;
import java.util.List;

/**
 * Dashboard service exposes methods to build the data shown in student dashboard.
 */
public interface DashboardService {
    /**
     * Build dashboard data for the given student id.
     *
     * @param studentId student's id
     * @return DashboardResponse DTO
     */
    DashboardResponse getDashboardForStudent(Long studentId);
    
    /**
     * Get upcoming events (public events for next 30 days)
     *
     * @return List of EventDTO
     */
    List<EventDTO> getUpcomingEvents();
    
    /**
     * Get system statistics for admin dashboard
     *
     * @return SystemStatsDTO with counts
     */
    SystemStatsDTO getSystemStats();
}
