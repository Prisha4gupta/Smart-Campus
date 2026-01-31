package com.sca.smartcampusbackend.dto;

import java.time.LocalTime;

/**
 * DTO representing the student's next upcoming class slot.
 */
public class NextClassDTO {
    private String subject;
    private String room;
    private String facultyName;
    private LocalTime startTime;
    private LocalTime endTime;

    public NextClassDTO() {}

    public NextClassDTO(String subject, String room, String facultyName, LocalTime startTime, LocalTime endTime) {
        this.subject = subject;
        this.room = room;
        this.facultyName = facultyName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters & setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getFacultyName() { return facultyName; }
    public void setFacultyName(String facultyName) { this.facultyName = facultyName; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
}
