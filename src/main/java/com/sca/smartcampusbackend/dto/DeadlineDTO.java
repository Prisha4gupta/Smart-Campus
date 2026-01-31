package com.sca.smartcampusbackend.dto;

import java.time.LocalDate;

/**
 * DTO representing a student's deadline/task in planner.
 */
public class DeadlineDTO {
    private Long id;
    private String title;
    private LocalDate dueDate;
    private String course;

    public DeadlineDTO() {}

    public DeadlineDTO(Long id, String title, LocalDate dueDate, String course) {
        this.id = id;
        this.title = title;
        this.dueDate = dueDate;
        this.course = course;
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
}
