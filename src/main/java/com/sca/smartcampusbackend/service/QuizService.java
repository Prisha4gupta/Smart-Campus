package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.quiz.QuizDTO;
import com.sca.smartcampusbackend.dto.quiz.QuizFormDTO;
import com.sca.smartcampusbackend.entity.Quiz;
import java.util.List;

public interface QuizService {
    List<QuizDTO> getAllQuizzesAsDTO();

    QuizDTO getQuizById(Long id);

    QuizDTO getQuizWithQuestions(Long id);

    QuizDTO getQuizWithAttempts(Long id);

    Quiz createQuiz(QuizFormDTO dto);

    Quiz updateQuiz(Long id, QuizFormDTO dto);

    void deleteQuiz(Long id);

    List<Quiz> getQuizzesForOffering(Long offeringId);
}
