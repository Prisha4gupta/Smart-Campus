package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuizFormDTO {
    private Long id;
    private Long offeringId;
    private String title;
    private String description;
    private LocalDateTime date;
    private Integer maxMarks;
}
