package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.entity.QuizAttempt;
import com.sca.smartcampusbackend.entity.QuizQuestion;
import com.sca.smartcampusbackend.entity.QuizAnswer;
import java.util.List;

public interface QuizAttemptService {
    QuizAttempt getAttempt(Long quizId, Long userId);

    List<QuizQuestion> getQuestions(Long quizId);

    List<QuizAnswer> getAnswersForAttempt(Long attemptId);

    void publishResults(Long quizId);
}
