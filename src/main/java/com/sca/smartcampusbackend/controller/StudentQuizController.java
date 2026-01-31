package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.quiz.QuizSummaryDTO;
import com.sca.smartcampusbackend.dto.quiz.QuestionReviewDTO;
import com.sca.smartcampusbackend.dto.quiz.QuizDTO;
import com.sca.smartcampusbackend.entity.Quiz;
import com.sca.smartcampusbackend.entity.QuizAttempt;
import com.sca.smartcampusbackend.entity.QuizQuestion;
import com.sca.smartcampusbackend.entity.QuizAnswer;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.QuizService;
import com.sca.smartcampusbackend.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@RequestMapping("/student")
public class StudentQuizController {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final QuizService quizService;
    private final QuizAttemptService attemptService;
    private final UserRepository userRepository;

    @GetMapping("/grades")
    public String getStudentGrades(Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();

        // Fetch all offerings the student is enrolled in
        List<Long> offeringIds = enrollmentRepository.findByUser_Id(userId)
                .stream()
                .map(e -> e.getOffering().getId())
                .distinct()
                .toList();

        List<QuizSummaryDTO> summaries = new ArrayList<>();

        for (Long offeringId : offeringIds) {
            List<Quiz> quizzes = quizService.getQuizzesForOffering(offeringId);
            for (Quiz quiz : quizzes) {
                QuizSummaryDTO dto = new QuizSummaryDTO();
                dto.setQuizId(quiz.getId());
                dto.setTitle(quiz.getTitle());
                dto.setDate(quiz.getDate());
                dto.setMaxMarks(quiz.getMaxMarks());

                try {
                    QuizAttempt attempt = attemptService.getAttempt(quiz.getId(), userId);
                    dto.setObtainedMarks(attempt.getObtainedMarks());
                    dto.setStatus(attempt.getStatus());
                } catch (Exception ex) {
                    dto.setStatus("NOT_ATTEMPTED");
                }

                summaries.add(dto);
            }
        }

        model.addAttribute("grades", summaries);
        return "pages/student/grades";
    }

    @GetMapping("/grades/{quizId}")
    public String getGradeDetails(@PathVariable Long quizId,
            Authentication authentication,
            Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long userId = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"))
                .getId();

        QuizDTO quizDTO = quizService.getQuizById(quizId);

        QuizAttempt attempt;
        try {
            attempt = attemptService.getAttempt(quizId, userId);
        } catch (Exception e) {
            return "redirect:/student/grades"; // Handle case where attempt doesn't exist
        }

        List<QuizQuestion> questions = attemptService.getQuestions(quizId);
        List<QuizAnswer> answers = attemptService.getAnswersForAttempt(attempt.getId());

        Map<Long, QuizAnswer> answerMap = new HashMap<>();
        for (QuizAnswer a : answers) {
            answerMap.put(a.getQuestion().getId(), a);
        }

        List<QuestionReviewDTO> reviewDTOs = new ArrayList<>();
        for (QuizQuestion q : questions) {
            QuestionReviewDTO dto = new QuestionReviewDTO();
            dto.setQuestionNumber(q.getQuestionNumber());
            dto.setMaxMarks(q.getMaxMarks());
            dto.setText(q.getText());

            QuizAnswer ans = answerMap.get(q.getId());
            if (ans != null) {
                dto.setObtainedMarks(ans.getObtainedMarks());
                dto.setEvaluatorComment(ans.getEvaluatorComment());
            } else {
                dto.setObtainedMarks(null);
                dto.setEvaluatorComment(null);
            }
            reviewDTOs.add(dto);
        }

        model.addAttribute("quiz", quizDTO);
        model.addAttribute("attempt", attempt);
        model.addAttribute("questions", reviewDTOs);
        return "pages/student/grade-details";
    }
}
