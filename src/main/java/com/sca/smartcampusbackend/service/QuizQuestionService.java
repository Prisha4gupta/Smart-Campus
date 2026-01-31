package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.quiz.QuizQuestionDTO;

public interface QuizQuestionService {
    QuizQuestionDTO createQuestion(Long quizId, QuizQuestionDTO dto);

    QuizQuestionDTO updateQuestion(Long questionId, QuizQuestionDTO dto);

    void deleteQuestion(Long questionId);

    int getNextQuestionNumber(Long quizId);
}
