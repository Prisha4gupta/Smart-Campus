package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionGradeDTO {
    private Long questionId;
    private Integer questionNumber;
    private String questionText;
    private Integer maxMarks;
    private Long answerId;
    private Integer obtainedMarks;
    private String evaluatorComment;
}
