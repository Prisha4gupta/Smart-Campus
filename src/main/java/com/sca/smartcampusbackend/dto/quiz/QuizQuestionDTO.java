package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO {
    private Long id;
    private Long quizId;
    private Integer questionNumber;
    private Integer maxMarks;
    private String text;
}
