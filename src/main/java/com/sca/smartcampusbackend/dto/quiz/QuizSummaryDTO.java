package com.sca.smartcampusbackend.dto.quiz;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSummaryDTO {
    private Long quizId;
    private String title;
    private LocalDateTime date;
    private Integer maxMarks;
    private Integer obtainedMarks;
    private String status;
}
