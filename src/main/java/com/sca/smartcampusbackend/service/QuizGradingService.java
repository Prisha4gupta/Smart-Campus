package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.quiz.GradeInputDTO;
import com.sca.smartcampusbackend.dto.quiz.QuestionGradeDTO;
import com.sca.smartcampusbackend.dto.quiz.StudentAttemptDTO;

import java.util.List;

public interface QuizGradingService {
    List<StudentAttemptDTO> getStudentAttemptsForQuiz(Long quizId);

    void markStudentAsAttempted(Long quizId, Long studentId);

    List<QuestionGradeDTO> getQuestionsForGrading(Long attemptId);

    void gradeQuestion(Long attemptId, GradeInputDTO dto);
}
