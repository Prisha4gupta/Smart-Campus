package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.quiz.GradeInputDTO;
import com.sca.smartcampusbackend.dto.quiz.QuestionGradeDTO;
import com.sca.smartcampusbackend.dto.quiz.StudentAttemptDTO;
import com.sca.smartcampusbackend.entity.*;
import com.sca.smartcampusbackend.repository.*;
import com.sca.smartcampusbackend.service.QuizGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizGradingServiceImpl implements QuizGradingService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;
    private final QuizQuestionRepository questionRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StudentAttemptDTO> getStudentAttemptsForQuiz(Long quizId) {
        Quiz quiz = quizRepository.findByIdWithOfferingAndCourse(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        Long offeringId = quiz.getOffering().getId();
        Integer maxMarks = quiz.getMaxMarks();

        // Get enrolled students
        List<StudentEnrollment> enrollments = enrollmentRepository.findEnrolledStudentsByOfferingIdWithUser(offeringId);

        // Get existing attempts
        List<QuizAttempt> attempts = attemptRepository.findByQuizIdWithUser(quizId);
        Map<Long, QuizAttempt> attemptsByUserId = attempts.stream()
                .collect(Collectors.toMap(a -> a.getUser().getId(), a -> a));

        // Build DTO list
        List<StudentAttemptDTO> result = new ArrayList<>();
        for (StudentEnrollment enrollment : enrollments) {
            User student = enrollment.getUser();
            QuizAttempt attempt = attemptsByUserId.get(student.getId());

            StudentAttemptDTO dto = new StudentAttemptDTO();
            dto.setStudentId(student.getId());
            dto.setStudentName(student.getUsername());
            dto.setStudentUsername(student.getUsername());
            dto.setMaxMarks(maxMarks);

            if (attempt == null) {
                dto.setStatus("NOT_ATTEMPTED");
                dto.setAttemptId(null);
                dto.setObtainedMarks(null);
            } else {
                dto.setAttemptId(attempt.getId());
                dto.setStatus(attempt.getStatus());
                dto.setObtainedMarks(attempt.getObtainedMarks());
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public void markStudentAsAttempted(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        // Check if attempt already exists
        Optional<QuizAttempt> existing = attemptRepository.findByQuiz_IdAndUser_Id(quizId, studentId);
        if (existing.isPresent()) {
            return; // Already marked
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuiz(quiz);
        attempt.setUser(student);
        attempt.setStatus("PENDING");
        attempt.setObtainedMarks(0);
        attemptRepository.save(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionGradeDTO> getQuestionsForGrading(Long attemptId) {
        QuizAttempt attempt = attemptRepository.findByIdWithAnswers(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + attemptId));

        Long quizId = attempt.getQuiz().getId();
        List<QuizQuestion> questions = questionRepository.findByQuiz_IdOrderByQuestionNumber(quizId);

        Map<Long, QuizAnswer> answersByQuestionId = new HashMap<>();
        if (attempt.getAnswers() != null) {
            answersByQuestionId = attempt.getAnswers().stream()
                    .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a));
        }

        List<QuestionGradeDTO> result = new ArrayList<>();
        for (QuizQuestion question : questions) {
            QuestionGradeDTO dto = new QuestionGradeDTO();
            dto.setQuestionId(question.getId());
            dto.setQuestionNumber(question.getQuestionNumber());
            dto.setQuestionText(question.getText());
            dto.setMaxMarks(question.getMaxMarks());

            QuizAnswer answer = answersByQuestionId.get(question.getId());
            if (answer != null) {
                dto.setAnswerId(answer.getId());
                dto.setObtainedMarks(answer.getObtainedMarks());
                dto.setEvaluatorComment(answer.getEvaluatorComment());
            }

            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional
    public void gradeQuestion(Long attemptId, GradeInputDTO dto) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalArgumentException("Attempt not found: " + attemptId));
        QuizQuestion question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + dto.getQuestionId()));

        // Upsert answer
        QuizAnswer answer = answerRepository.findByAttempt_IdAndQuestion_Id(attemptId, dto.getQuestionId())
                .orElseGet(() -> {
                    QuizAnswer newAnswer = new QuizAnswer();
                    newAnswer.setAttempt(attempt);
                    newAnswer.setQuestion(question);
                    return newAnswer;
                });

        answer.setObtainedMarks(dto.getObtainedMarks());
        answer.setEvaluatorComment(dto.getEvaluatorComment());
        answerRepository.save(answer);

        // Recalculate total obtained marks
        List<QuizAnswer> allAnswers = answerRepository.findByAttempt_Id(attemptId);
        int totalObtained = allAnswers.stream()
                .filter(a -> a.getObtainedMarks() != null)
                .mapToInt(QuizAnswer::getObtainedMarks)
                .sum();
        attempt.setObtainedMarks(totalObtained);

        // Check if all questions are graded
        List<QuizQuestion> questions = questionRepository.findByQuiz_IdOrderByQuestionNumber(attempt.getQuiz().getId());
        long gradedCount = allAnswers.stream()
                .filter(a -> a.getObtainedMarks() != null)
                .count();

        if (gradedCount >= questions.size()) {
            attempt.setStatus("GRADED");
        }

        attemptRepository.save(attempt);
    }
}
