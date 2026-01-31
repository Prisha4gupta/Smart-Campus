package com.sca.smartcampusbackend.dto;

import java.util.List;

/**
 * Payload returned by the dashboard API for a student.
 */
public class DashboardResponse {
    private NextClassDTO nextClass;
    private List<DeadlineDTO> upcomingDeadlines;
    private List<EventDTO> upcomingEvents;
    private List<String> recommendations;

    public DashboardResponse() {}

    public DashboardResponse(NextClassDTO nextClass, List<DeadlineDTO> upcomingDeadlines,
                             List<EventDTO> upcomingEvents, List<String> recommendations) {
        this.nextClass = nextClass;
        this.upcomingDeadlines = upcomingDeadlines;
        this.upcomingEvents = upcomingEvents;
        this.recommendations = recommendations;
    }

    // Getters & setters
    public NextClassDTO getNextClass() { return nextClass; }
    public void setNextClass(NextClassDTO nextClass) { this.nextClass = nextClass; }

    public List<DeadlineDTO> getUpcomingDeadlines() { return upcomingDeadlines; }
    public void setUpcomingDeadlines(List<DeadlineDTO> upcomingDeadlines) { this.upcomingDeadlines = upcomingDeadlines; }

    public List<EventDTO> getUpcomingEvents() { return upcomingEvents; }
    public void setUpcomingEvents(List<EventDTO> upcomingEvents) { this.upcomingEvents = upcomingEvents; }

    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
