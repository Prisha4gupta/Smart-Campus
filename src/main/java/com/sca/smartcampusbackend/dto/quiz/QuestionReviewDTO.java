package com.sca.smartcampusbackend.dto.quiz;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionReviewDTO {
    private Integer questionNumber;
    private Integer maxMarks;
    private Integer obtainedMarks;
    private String text;
    private String evaluatorComment;
}
