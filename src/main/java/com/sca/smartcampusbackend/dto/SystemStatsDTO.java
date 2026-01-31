package com.sca.smartcampusbackend.dto;

/**
 * DTO for system overview statistics on admin dashboard
 */
public class SystemStatsDTO {
    private Long totalCourses;
    private Long totalStudents;
    private Long totalFaculty;
    private Long activeEvents;

    public SystemStatsDTO() {
    }

    public SystemStatsDTO(Long totalCourses, Long totalStudents, Long totalFaculty, Long activeEvents) {
        this.totalCourses = totalCourses;
        this.totalStudents = totalStudents;
        this.totalFaculty = totalFaculty;
        this.activeEvents = activeEvents;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(Long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public Long getActiveEvents() {
        return activeEvents;
    }

    public void setActiveEvents(Long activeEvents) {
        this.activeEvents = activeEvents;
    }
}
