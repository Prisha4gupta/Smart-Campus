package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentAttemptDTO {
    private Long studentId;
    private String studentName;
    private String studentUsername;
    private Long attemptId;
    private String status; // NOT_ATTEMPTED, PENDING, GRADED
    private Integer obtainedMarks;
    private Integer maxMarks;
}
