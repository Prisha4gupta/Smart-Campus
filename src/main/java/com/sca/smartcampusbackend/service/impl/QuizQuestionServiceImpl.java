package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.quiz.QuizQuestionDTO;
import com.sca.smartcampusbackend.entity.Quiz;
import com.sca.smartcampusbackend.entity.QuizQuestion;
import com.sca.smartcampusbackend.repository.QuizQuestionRepository;
import com.sca.smartcampusbackend.repository.QuizRepository;
import com.sca.smartcampusbackend.service.QuizQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizQuestionServiceImpl implements QuizQuestionService {

    private final QuizQuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    @Transactional
    public QuizQuestionDTO createQuestion(Long quizId, QuizQuestionDTO dto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        // Validate marks won't exceed quiz max marks
        int currentTotal = questionRepository.sumMaxMarksByQuizId(quizId);
        int newMarks = dto.getMaxMarks() != null ? dto.getMaxMarks() : 0;

        if (quiz.getMaxMarks() > 0 && (currentTotal + newMarks) > quiz.getMaxMarks()) {
            throw new IllegalArgumentException(
                    "Cannot add question: Total marks (" + (currentTotal + newMarks) +
                            ") would exceed quiz max marks (" + quiz.getMaxMarks() + ")");
        }

        QuizQuestion question = new QuizQuestion();
        question.setQuiz(quiz);
        question.setQuestionNumber(
                dto.getQuestionNumber() != null ? dto.getQuestionNumber() : getNextQuestionNumber(quizId));
        question.setMaxMarks(newMarks);
        question.setText(dto.getText());

        QuizQuestion saved = questionRepository.save(question);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public QuizQuestionDTO updateQuestion(Long questionId, QuizQuestionDTO dto) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        Quiz quiz = quizRepository.findById(question.getQuiz().getId())
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        // Validate marks won't exceed quiz max marks
        int currentTotal = questionRepository.sumMaxMarksByQuizId(quiz.getId());
        int oldMarks = question.getMaxMarks();
        int newMarks = dto.getMaxMarks() != null ? dto.getMaxMarks() : 0;
        int updatedTotal = currentTotal - oldMarks + newMarks;

        if (quiz.getMaxMarks() > 0 && updatedTotal > quiz.getMaxMarks()) {
            throw new IllegalArgumentException(
                    "Cannot update question: Total marks (" + updatedTotal +
                            ") would exceed quiz max marks (" + quiz.getMaxMarks() + ")");
        }

        question.setQuestionNumber(dto.getQuestionNumber());
        question.setMaxMarks(newMarks);
        question.setText(dto.getText());

        QuizQuestion saved = questionRepository.save(question);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        QuizQuestion question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        questionRepository.delete(question);
    }

    @Override
    public int getNextQuestionNumber(Long quizId) {
        List<QuizQuestion> questions = questionRepository.findByQuiz_IdOrderByQuestionNumber(quizId);
        return questions.isEmpty() ? 1 : questions.get(questions.size() - 1).getQuestionNumber() + 1;
    }

    private QuizQuestionDTO toDTO(QuizQuestion q) {
        QuizQuestionDTO dto = new QuizQuestionDTO();
        dto.setId(q.getId());
        dto.setQuizId(q.getQuiz().getId());
        dto.setQuestionNumber(q.getQuestionNumber());
        dto.setMaxMarks(q.getMaxMarks());
        dto.setText(q.getText());
        return dto;
    }
}
