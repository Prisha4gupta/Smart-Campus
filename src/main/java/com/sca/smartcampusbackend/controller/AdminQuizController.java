package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.quiz.*;
import com.sca.smartcampusbackend.entity.CourseOffering;
import com.sca.smartcampusbackend.entity.Quiz;
import com.sca.smartcampusbackend.repository.CourseOfferingRepository;
import com.sca.smartcampusbackend.service.QuizService;
import com.sca.smartcampusbackend.service.QuizQuestionService;
import com.sca.smartcampusbackend.service.QuizGradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/quizzes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQuizController {

    private final QuizService quizService;
    private final QuizQuestionService questionService;
    private final QuizGradingService gradingService;
    private final CourseOfferingRepository offeringRepository;

    @GetMapping
    public String listQuizzes(Model model) {
        List<QuizDTO> quizzes = quizService.getAllQuizzesAsDTO();
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/list";
    }

    @GetMapping("/create")
    public String createQuizForm(Model model) {
        List<CourseOffering> offerings = offeringRepository.findAllWithCourseAndFaculty();
        model.addAttribute("offerings", offerings);
        model.addAttribute("quiz", new QuizFormDTO());
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/create";
    }

    @PostMapping
    public String createQuiz(@ModelAttribute QuizFormDTO dto, RedirectAttributes redirectAttributes) {
        try {
            Quiz quiz = quizService.createQuiz(dto);
            redirectAttributes.addFlashAttribute("success", "Quiz created successfully!");
            return "redirect:/admin/quizzes/" + quiz.getId() + "/questions";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create quiz: " + e.getMessage());
            return "redirect:/admin/quizzes/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editQuizForm(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizById(id);
        List<CourseOffering> offerings = offeringRepository.findAllWithCourseAndFaculty();

        QuizFormDTO formDTO = new QuizFormDTO();
        formDTO.setId(quiz.getId());
        formDTO.setOfferingId(quiz.getOfferingId());
        formDTO.setTitle(quiz.getTitle());
        formDTO.setDescription(quiz.getDescription());
        formDTO.setDate(quiz.getDate());
        formDTO.setMaxMarks(quiz.getMaxMarks());

        model.addAttribute("quiz", formDTO);
        model.addAttribute("offerings", offerings);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/edit";
    }

    @PostMapping("/{id}/update")
    public String updateQuiz(@PathVariable Long id, @ModelAttribute QuizFormDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            quizService.updateQuiz(id, dto);
            redirectAttributes.addFlashAttribute("success", "Quiz updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update quiz: " + e.getMessage());
        }
        return "redirect:/admin/quizzes";
    }

    @PostMapping("/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id);
            redirectAttributes.addFlashAttribute("success", "Quiz deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete quiz: " + e.getMessage());
        }
        return "redirect:/admin/quizzes";
    }

    // ===== QUESTIONS =====

    @GetMapping("/{id}/questions")
    public String listQuestions(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizWithQuestions(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quiz.getQuestions());
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/questions";
    }

    @GetMapping("/{id}/questions/create")
    public String createQuestionForm(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizById(id);
        int nextNumber = questionService.getNextQuestionNumber(id);

        QuizQuestionDTO dto = new QuizQuestionDTO();
        dto.setQuizId(id);
        dto.setQuestionNumber(nextNumber);

        model.addAttribute("quiz", quiz);
        model.addAttribute("question", dto);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/create-question";
    }

    @PostMapping("/{id}/questions")
    public String createQuestion(@PathVariable Long id, @ModelAttribute QuizQuestionDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.createQuestion(id, dto);
            redirectAttributes.addFlashAttribute("success", "Question added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add question: " + e.getMessage());
        }
        return "redirect:/admin/quizzes/" + id + "/questions";
    }

    @GetMapping("/{quizId}/questions/{questionId}/edit")
    public String editQuestionForm(@PathVariable Long quizId, @PathVariable Long questionId, Model model) {
        QuizDTO quiz = quizService.getQuizWithQuestions(quizId);
        QuizQuestionDTO question = quiz.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        model.addAttribute("quiz", quiz);
        model.addAttribute("question", question);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/edit-question";
    }

    @PostMapping("/{quizId}/questions/{questionId}/update")
    public String updateQuestion(@PathVariable Long quizId, @PathVariable Long questionId,
            @ModelAttribute QuizQuestionDTO dto, RedirectAttributes redirectAttributes) {
        try {
            questionService.updateQuestion(questionId, dto);
            redirectAttributes.addFlashAttribute("success", "Question updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update question: " + e.getMessage());
        }
        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    @PostMapping("/{quizId}/questions/{questionId}/delete")
    public String deleteQuestion(@PathVariable Long quizId, @PathVariable Long questionId,
            RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(questionId);
            redirectAttributes.addFlashAttribute("success", "Question deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete question: " + e.getMessage());
        }
        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    // ===== ATTEMPTS (FEATURE 1 & 2) =====

    @GetMapping("/{id}/attempts")
    public String viewAttempts(@PathVariable Long id, Model model) {
        QuizDTO quiz = quizService.getQuizById(id);
        List<StudentAttemptDTO> attempts = gradingService.getStudentAttemptsForQuiz(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("attempts", attempts);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/manage-attempts";
    }

    @PostMapping("/{quizId}/attempts/{studentId}/mark")
    public String markAsAttempted(@PathVariable Long quizId, @PathVariable Long studentId,
            RedirectAttributes redirectAttributes) {
        try {
            gradingService.markStudentAsAttempted(quizId, studentId);
            redirectAttributes.addFlashAttribute("success", "Student marked as attempted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to mark attempt: " + e.getMessage());
        }
        return "redirect:/admin/quizzes/" + quizId + "/attempts";
    }

    @GetMapping("/{quizId}/attempts/{attemptId}/questions")
    public String gradeAttemptForm(@PathVariable Long quizId, @PathVariable Long attemptId, Model model) {
        QuizDTO quiz = quizService.getQuizById(quizId);
        List<QuestionGradeDTO> questions = gradingService.getQuestionsForGrading(attemptId);

        model.addAttribute("quiz", quiz);
        model.addAttribute("attemptId", attemptId);
        model.addAttribute("questions", questions);
        model.addAttribute("currentPage", "admin/quizzes");
        return "admin/quizzes/grade-attempt";
    }

    @PostMapping("/{quizId}/attempts/{attemptId}/questions/{questionId}/grade")
    public String gradeQuestion(@PathVariable Long quizId, @PathVariable Long attemptId,
            @PathVariable Long questionId, @ModelAttribute GradeInputDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            dto.setQuestionId(questionId);
            gradingService.gradeQuestion(attemptId, dto);
            redirectAttributes.addFlashAttribute("success", "Question graded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to grade question: " + e.getMessage());
        }
        return "redirect:/admin/quizzes/" + quizId + "/attempts/" + attemptId + "/questions";
    }
}
