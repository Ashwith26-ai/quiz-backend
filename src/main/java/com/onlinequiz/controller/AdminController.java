package com.onlinequiz.controller;

import com.onlinequiz.dto.DashboardStatsDto;
import com.onlinequiz.dto.QuestionFormDto;
import com.onlinequiz.dto.QuizFormDto;
import com.onlinequiz.entity.Quiz;
import com.onlinequiz.entity.QuizAttempt;
import com.onlinequiz.entity.User;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.service.QuestionService;
import com.onlinequiz.service.QuizAttemptService;
import com.onlinequiz.service.QuizService;
import com.onlinequiz.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QuizService quizService;
    private final QuestionService questionService;
    private final UserService userService;
    private final QuizAttemptService quizAttemptService;

    public AdminController(QuizService quizService,
                           QuestionService questionService,
                           UserService userService,
                           QuizAttemptService quizAttemptService) {
        this.quizService = quizService;
        this.questionService = questionService;
        this.userService = userService;
        this.quizAttemptService = quizAttemptService;
    }

    private User getAuthenticatedAdmin(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("No active session found.");
        }
        return userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        DashboardStatsDto stats = quizAttemptService.getDashboardStatistics();
        List<QuizAttempt> recentAttempts = quizAttemptService.getAllAttempts().stream().limit(10).toList();

        model.addAttribute("admin", admin);
        model.addAttribute("stats", stats);
        model.addAttribute("recentAttempts", recentAttempts);

        return "admin/dashboard";
    }

    // --- Quiz Management ---

    @GetMapping("/quizzes")
    public String listQuizzes(Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        List<Quiz> quizzes = quizService.getAllQuizzes();

        model.addAttribute("admin", admin);
        model.addAttribute("quizzes", quizzes);

        return "admin/quizzes";
    }

    @GetMapping("/quizzes/new")
    public String newQuizForm(Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        QuizFormDto formDto = new QuizFormDto();
        formDto.setDurationMinutes(10);
        formDto.setTotalMarks(5);
        formDto.setPassingMarks(3);
        formDto.setActive(true);

        model.addAttribute("admin", admin);
        model.addAttribute("quizForm", formDto);
        model.addAttribute("isEdit", false);

        return "admin/quiz-form";
    }

    @PostMapping("/quizzes/save")
    public String saveQuiz(@Valid @ModelAttribute("quizForm") QuizFormDto quizForm,
                           BindingResult bindingResult,
                           Authentication authentication,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            User admin = getAuthenticatedAdmin(authentication);
            model.addAttribute("admin", admin);
            model.addAttribute("isEdit", quizForm.getId() != null);
            return "admin/quiz-form";
        }

        if (quizForm.getId() == null) {
            quizService.createQuiz(quizForm);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz created successfully!");
        } else {
            quizService.updateQuiz(quizForm.getId(), quizForm);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz updated successfully!");
        }

        return "redirect:/admin/quizzes";
    }

    @GetMapping("/quizzes/edit/{id}")
    public String editQuizForm(@PathVariable Long id, Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        QuizFormDto formDto = quizService.getQuizFormDto(id);

        model.addAttribute("admin", admin);
        model.addAttribute("quizForm", formDto);
        model.addAttribute("isEdit", true);

        return "admin/quiz-form";
    }

    @GetMapping("/quizzes/delete/{id}")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.deleteQuiz(id);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete quiz: " + ex.getMessage());
        }
        return "redirect:/admin/quizzes";
    }

    @GetMapping("/quizzes/toggle/{id}")
    public String toggleQuizStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            quizService.toggleQuizStatus(id);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz status updated.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating status: " + ex.getMessage());
        }
        return "redirect:/admin/quizzes";
    }

    // --- Question Management ---

    @GetMapping("/quizzes/{quizId}/questions")
    public String listQuestions(@PathVariable Long quizId, Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        Quiz quiz = quizService.getQuizById(quizId);
        List<com.onlinequiz.entity.Question> questions = questionService.getQuestionsByQuizId(quizId);

        model.addAttribute("admin", admin);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);

        return "admin/questions";
    }

    @GetMapping("/quizzes/{quizId}/questions/new")
    public String newQuestionForm(@PathVariable Long quizId, Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        Quiz quiz = quizService.getQuizById(quizId);
        QuestionFormDto formDto = new QuestionFormDto(quizId);

        model.addAttribute("admin", admin);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionForm", formDto);
        model.addAttribute("isEdit", false);

        return "admin/question-form";
    }

    @PostMapping("/quizzes/{quizId}/questions/save")
    public String saveQuestion(@PathVariable Long quizId,
                               @Valid @ModelAttribute("questionForm") QuestionFormDto questionForm,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        Quiz quiz = quizService.getQuizById(quizId);
        questionForm.setQuizId(quizId);

        if (bindingResult.hasErrors()) {
            User admin = getAuthenticatedAdmin(authentication);
            model.addAttribute("admin", admin);
            model.addAttribute("quiz", quiz);
            model.addAttribute("isEdit", questionForm.getId() != null);
            return "admin/question-form";
        }

        if (questionForm.getId() == null) {
            questionService.createQuestion(questionForm);
            redirectAttributes.addFlashAttribute("successMessage", "Question added successfully!");
        } else {
            questionService.updateQuestion(questionForm.getId(), questionForm);
            redirectAttributes.addFlashAttribute("successMessage", "Question updated successfully!");
        }

        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    @GetMapping("/quizzes/{quizId}/questions/edit/{questionId}")
    public String editQuestionForm(@PathVariable Long quizId,
                                   @PathVariable Long questionId,
                                   Authentication authentication,
                                   Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        Quiz quiz = quizService.getQuizById(quizId);
        QuestionFormDto formDto = questionService.getQuestionFormDto(questionId);

        model.addAttribute("admin", admin);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionForm", formDto);
        model.addAttribute("isEdit", true);

        return "admin/question-form";
    }

    @GetMapping("/quizzes/{quizId}/questions/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long quizId,
                                 @PathVariable Long questionId,
                                 RedirectAttributes redirectAttributes) {
        try {
            questionService.deleteQuestion(questionId);
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting question: " + ex.getMessage());
        }
        return "redirect:/admin/quizzes/" + quizId + "/questions";
    }

    // --- Student Management ---

    @GetMapping("/students")
    public String listStudents(Authentication authentication, Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        List<User> students = userService.getAllStudents();

        model.addAttribute("admin", admin);
        model.addAttribute("students", students);

        return "admin/students";
    }

    @GetMapping("/students/{studentId}/attempts")
    public String studentAttempts(@PathVariable Long studentId,
                                  Authentication authentication,
                                  Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        User student = userService.getStudentById(studentId);
        List<QuizAttempt> attempts = quizAttemptService.getStudentAttempts(studentId);

        model.addAttribute("admin", admin);
        model.addAttribute("student", student);
        model.addAttribute("attempts", attempts);

        return "admin/student-attempts";
    }

    // --- Results & Reports ---

    @GetMapping("/results")
    public String listResults(@RequestParam(value = "quizId", required = false) Long quizId,
                              Authentication authentication,
                              Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        List<QuizAttempt> attempts;
        if (quizId != null && quizId > 0) {
            attempts = quizAttemptService.getAttemptsByQuiz(quizId);
        } else {
            attempts = quizAttemptService.getAllAttempts();
        }

        List<Quiz> quizzes = quizService.getAllQuizzes();

        model.addAttribute("admin", admin);
        model.addAttribute("attempts", attempts);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("selectedQuizId", quizId);

        return "admin/results";
    }

    @GetMapping("/results/{attemptId}")
    public String viewAttemptDetails(@PathVariable Long attemptId,
                                     Authentication authentication,
                                     Model model) {
        User admin = getAuthenticatedAdmin(authentication);
        QuizAttempt attempt = quizAttemptService.getAttemptById(attemptId);

        model.addAttribute("admin", admin);
        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("student", attempt.getStudent());
        model.addAttribute("answers", attempt.getAnswers());

        return "admin/attempt-details";
    }
}
