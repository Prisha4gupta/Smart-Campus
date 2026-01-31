package com.sca.smartcampusbackend.dto.quiz;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime date;
    private Integer maxMarks;
    private Integer totalQuestionMarks; // Sum of all question marks
    private Long offeringId;
    private String offeringCode;
    private String offeringTitle;
    private String offeringSemester;
    private Integer questionCount;
    private Integer attemptCount;
    private List<QuizQuestionDTO> questions = new ArrayList<>();
    private List<QuizAttemptDTO> attempts = new ArrayList<>();
}
