package com.sca.smartcampusbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @Column(name = "obtained_marks")
    private Integer obtainedMarks;

    @Column(name = "evaluator_comment", columnDefinition = "TEXT")
    private String evaluatorComment;
}
