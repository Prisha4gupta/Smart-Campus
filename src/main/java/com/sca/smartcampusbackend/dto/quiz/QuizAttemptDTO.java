package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttemptDTO {
    private Long id;
    private Long quizId;
    private Long userId;
    private String username;
    private Integer obtainedMarks;
    private String status;
}
