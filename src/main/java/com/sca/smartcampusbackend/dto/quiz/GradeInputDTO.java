package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GradeInputDTO {
    private Long questionId;
    private Integer obtainedMarks;
    private String evaluatorComment;
}
