package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.quiz.QuizAttemptDTO;
import com.sca.smartcampusbackend.dto.quiz.QuizDTO;
import com.sca.smartcampusbackend.dto.quiz.QuizFormDTO;
import com.sca.smartcampusbackend.dto.quiz.QuizQuestionDTO;
import com.sca.smartcampusbackend.entity.CourseOffering;
import com.sca.smartcampusbackend.entity.Quiz;
import com.sca.smartcampusbackend.entity.QuizAttempt;
import com.sca.smartcampusbackend.entity.QuizQuestion;
import com.sca.smartcampusbackend.repository.CourseOfferingRepository;
import com.sca.smartcampusbackend.repository.QuizQuestionRepository;
import com.sca.smartcampusbackend.repository.QuizRepository;
import com.sca.smartcampusbackend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseOfferingRepository offeringRepository;
    private final QuizQuestionRepository quizQuestionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<QuizDTO> getAllQuizzesAsDTO() {
        return quizRepository.findAllWithOfferingAndCourse().stream()
                .map(this::toBasicDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findByIdWithOfferingAndCourse(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
        return toBasicDTO(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDTO getQuizWithQuestions(Long id) {
        Quiz quiz = quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
        return toDTOWithQuestions(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizDTO getQuizWithAttempts(Long id) {
        Quiz quiz = quizRepository.findByIdWithAttempts(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
        return toDTOWithAttempts(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getQuizzesForOffering(Long offeringId) {
        return quizRepository.findByOffering_Id(offeringId);
    }

    @Override
    @Transactional
    public Quiz createQuiz(QuizFormDTO dto) {
        CourseOffering offering = offeringRepository.findById(dto.getOfferingId())
                .orElseThrow(() -> new IllegalArgumentException("Offering not found: " + dto.getOfferingId()));
        Quiz quiz = new Quiz();
        quiz.setOffering(offering);
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setDate(dto.getDate());
        quiz.setMaxMarks(dto.getMaxMarks() != null ? dto.getMaxMarks() : 0);
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public Quiz updateQuiz(Long id, QuizFormDTO dto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
        if (dto.getOfferingId() != null) {
            CourseOffering offering = offeringRepository.findById(dto.getOfferingId())
                    .orElseThrow(() -> new IllegalArgumentException("Offering not found"));
            quiz.setOffering(offering);
        }
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setDate(dto.getDate());
        if (dto.getMaxMarks() != null) {
            quiz.setMaxMarks(dto.getMaxMarks());
        }
        return quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + id));
        quizRepository.delete(quiz);
    }

    private QuizDTO toBasicDTO(Quiz quiz) {
        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDate(quiz.getDate());
        dto.setMaxMarks(quiz.getMaxMarks()); // Keep the quiz's configured max marks

        if (quiz.getOffering() != null) {
            dto.setOfferingId(quiz.getOffering().getId());
            dto.setOfferingSemester(quiz.getOffering().getSemester());
            if (quiz.getOffering().getCourse() != null) {
                dto.setOfferingCode(quiz.getOffering().getCourse().getCode());
                dto.setOfferingTitle(quiz.getOffering().getCourse().getTitle());
            } else {
                dto.setOfferingCode("N/A");
                dto.setOfferingTitle("Unknown");
            }
        } else {
            dto.setOfferingCode("N/A");
            dto.setOfferingTitle("No Offering");
            dto.setOfferingSemester("N/A");
        }

        // Use repository queries to avoid lazy loading
        dto.setQuestionCount(quizQuestionRepository.countByQuizId(quiz.getId()));
        dto.setTotalQuestionMarks(quizQuestionRepository.sumMaxMarksByQuizId(quiz.getId()));
        dto.setAttemptCount(0);
        return dto;
    }

    private QuizDTO toDTOWithQuestions(Quiz quiz) {
        QuizDTO dto = toBasicDTO(quiz);
        if (quiz.getQuestions() != null) {
            dto.setQuestionCount(quiz.getQuestions().size());
            dto.setQuestions(quiz.getQuestions().stream()
                    .sorted(Comparator.comparingInt(QuizQuestion::getQuestionNumber))
                    .map(this::toQuestionDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private QuizDTO toDTOWithAttempts(Quiz quiz) {
        QuizDTO dto = toBasicDTO(quiz);
        if (quiz.getAttempts() != null) {
            dto.setAttemptCount(quiz.getAttempts().size());
            dto.setAttempts(quiz.getAttempts().stream()
                    .map(this::toAttemptDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private QuizQuestionDTO toQuestionDTO(QuizQuestion q) {
        QuizQuestionDTO dto = new QuizQuestionDTO();
        dto.setId(q.getId());
        dto.setQuizId(q.getQuiz().getId());
        dto.setQuestionNumber(q.getQuestionNumber());
        dto.setMaxMarks(q.getMaxMarks());
        dto.setText(q.getText());
        return dto;
    }

    private QuizAttemptDTO toAttemptDTO(QuizAttempt a) {
        QuizAttemptDTO dto = new QuizAttemptDTO();
        dto.setId(a.getId());
        dto.setQuizId(a.getQuiz().getId());
        if (a.getUser() != null) {
            dto.setUserId(a.getUser().getId());
            dto.setUsername(a.getUser().getUsername());
        }
        dto.setObtainedMarks(a.getObtainedMarks());
        dto.setStatus(a.getStatus());
        return dto;
    }
}
