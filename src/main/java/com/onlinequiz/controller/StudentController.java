package com.onlinequiz.controller;

import com.onlinequiz.dto.QuestionAnswerSubmission;
import com.onlinequiz.dto.QuestionResponseDto;
import com.onlinequiz.dto.QuizSubmissionDto;
import com.onlinequiz.entity.AttemptStatus;
import com.onlinequiz.entity.Quiz;
import com.onlinequiz.entity.QuizAttempt;
import com.onlinequiz.entity.User;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.service.QuestionService;
import com.onlinequiz.service.QuizAttemptService;
import com.onlinequiz.service.QuizService;
import com.onlinequiz.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final QuizService quizService;
    private final QuestionService questionService;
    private final QuizAttemptService quizAttemptService;

    public StudentController(UserService userService,
                             QuizService quizService,
                             QuestionService questionService,
                             QuizAttemptService quizAttemptService) {
        this.userService = userService;
        this.quizService = quizService;
        this.questionService = questionService;
        this.quizAttemptService = quizAttemptService;
    }

    private User getAuthenticatedStudent(Authentication authentication) {
        if (authentication == null) {
            throw new ResourceNotFoundException("No active session found.");
        }
        return userService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found for current session."));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        User student = getAuthenticatedStudent(authentication);
        List<QuizAttempt> attempts = quizAttemptService.getStudentAttempts(student.getId());

        long totalAttempts = attempts.size();
        long passedCount = attempts.stream().filter(a -> a.getStatus() == AttemptStatus.PASSED).count();
        double avgScore = attempts.stream()
                .filter(a -> a.getStatus() != AttemptStatus.IN_PROGRESS)
                .mapToDouble(QuizAttempt::getPercentage)
                .average()
                .orElse(0.0);

        List<Quiz> activeQuizzes = quizService.getActiveQuizzes();

        model.addAttribute("student", student);
        model.addAttribute("totalAttempts", totalAttempts);
        model.addAttribute("passedCount", passedCount);
        model.addAttribute("avgScore", Math.round(avgScore * 10.0) / 10.0);
        model.addAttribute("activeQuizzesCount", activeQuizzes.size());
        model.addAttribute("recentAttempts", attempts.stream().limit(5).toList());

        return "student/dashboard";
    }

    @GetMapping("/quizzes")
    public String availableQuizzes(Authentication authentication, Model model) {
        User student = getAuthenticatedStudent(authentication);
        List<Quiz> activeQuizzes = quizService.getActiveQuizzes();
        model.addAttribute("student", student);
        model.addAttribute("quizzes", activeQuizzes);
        return "student/quizzes";
    }

    @GetMapping("/quiz/{id}/instructions")
    public String quizInstructions(@PathVariable Long id, Authentication authentication, Model model) {
        User student = getAuthenticatedStudent(authentication);
        Quiz quiz = quizService.getQuizById(id);
        long questionCount = questionService.getQuestionsByQuizId(id).size();

        model.addAttribute("student", student);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionCount", questionCount);

        return "student/quiz-instructions";
    }

    @GetMapping("/quiz/{id}/start")
    public String startQuiz(@PathVariable Long id,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        User student = getAuthenticatedStudent(authentication);
        try {
            QuizAttempt attempt = quizAttemptService.startQuizAttempt(student.getId(), id);
            return "redirect:/student/quiz/attempt/" + attempt.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/quizzes";
        }
    }

    @GetMapping("/quiz/attempt/{attemptId}")
    public String takeQuiz(@PathVariable Long attemptId,
                           Authentication authentication,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        User student = getAuthenticatedStudent(authentication);
        QuizAttempt attempt = quizAttemptService.getAttemptById(attemptId);

        if (!attempt.getStudent().getId().equals(student.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied for this attempt.");
            return "redirect:/student/dashboard";
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return "redirect:/student/quiz/result/" + attempt.getId();
        }

        long remainingSeconds = quizAttemptService.getRemainingTimeSeconds(attempt);
        if (remainingSeconds <= 0) {
            // Time expired, auto-submit
            QuizSubmissionDto emptyDto = new QuizSubmissionDto(attempt.getId());
            quizAttemptService.submitQuiz(student.getId(), emptyDto);
            return "redirect:/student/quiz/result/" + attempt.getId();
        }

        List<QuestionResponseDto> questions = questionService.getSanitizedQuestionsForQuiz(attempt.getQuiz().getId());

        QuizSubmissionDto submissionDto = new QuizSubmissionDto(attempt.getId());
        List<QuestionAnswerSubmission> answers = new ArrayList<>();
        for (QuestionResponseDto q : questions) {
            answers.add(new QuestionAnswerSubmission(q.getId(), null));
        }
        submissionDto.setAnswers(answers);

        model.addAttribute("student", student);
        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("questions", questions);
        model.addAttribute("remainingSeconds", remainingSeconds);
        model.addAttribute("submissionDto", submissionDto);

        return "student/quiz-take";
    }

    @PostMapping("/quiz/submit")
    public String submitQuiz(@ModelAttribute QuizSubmissionDto submissionDto,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User student = getAuthenticatedStudent(authentication);
        try {
            QuizAttempt completedAttempt = quizAttemptService.submitQuiz(student.getId(), submissionDto);
            redirectAttributes.addFlashAttribute("successMessage", "Quiz submitted successfully!");
            return "redirect:/student/quiz/result/" + completedAttempt.getId();
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/dashboard";
        }
    }

    @GetMapping("/quiz/result/{attemptId}")
    public String quizResult(@PathVariable Long attemptId,
                             Authentication authentication,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        User student = getAuthenticatedStudent(authentication);
        QuizAttempt attempt = quizAttemptService.getAttemptById(attemptId);

        if (!attempt.getStudent().getId().equals(student.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied for this result.");
            return "redirect:/student/dashboard";
        }

        model.addAttribute("student", student);
        model.addAttribute("attempt", attempt);
        model.addAttribute("quiz", attempt.getQuiz());
        model.addAttribute("answers", attempt.getAnswers());

        return "student/quiz-result";
    }

    @GetMapping("/history")
    public String quizHistory(Authentication authentication, Model model) {
        User student = getAuthenticatedStudent(authentication);
        List<QuizAttempt> attempts = quizAttemptService.getStudentAttempts(student.getId());

        model.addAttribute("student", student);
        model.addAttribute("attempts", attempts);

        return "student/quiz-history";
    }
}
