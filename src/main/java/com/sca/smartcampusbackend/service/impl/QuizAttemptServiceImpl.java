package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.entity.QuizAttempt;
import com.sca.smartcampusbackend.entity.QuizQuestion;
import com.sca.smartcampusbackend.entity.QuizAnswer;
import com.sca.smartcampusbackend.repository.QuizAttemptRepository;
import com.sca.smartcampusbackend.repository.QuizQuestionRepository;
import com.sca.smartcampusbackend.repository.QuizAnswerRepository;
import com.sca.smartcampusbackend.service.NotificationService;
import com.sca.smartcampusbackend.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository attemptRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAnswerRepository answerRepository;
    private final NotificationService notificationService;

    @Override
    public QuizAttempt getAttempt(Long quizId, Long userId) {
        return attemptRepository.findByQuiz_IdAndUser_Id(quizId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Attempt not found for quizId: " + quizId + " and userId: " + userId));
    }

    @Override
    public List<QuizQuestion> getQuestions(Long quizId) {
        return questionRepository.findByQuiz_IdOrderByQuestionNumber(quizId);
    }

    @Override
    public List<QuizAnswer> getAnswersForAttempt(Long attemptId) {
        return answerRepository.findByAttempt_Id(attemptId);
    }

    @Override
    public void publishResults(Long quizId) {
        List<QuizAttempt> attempts = attemptRepository.findByQuiz_Id(quizId);

        for (QuizAttempt attempt : attempts) {
            attempt.setStatus("PUBLISHED");
            attemptRepository.save(attempt);

            Long userId = attempt.getUser().getId();
            String quizTitle = attempt.getQuiz().getTitle();
            notificationService.createNotification(
                    userId,
                    null,
                    "Quiz Results Published",
                    quizTitle + " results are now available.");
        }
    }
}
