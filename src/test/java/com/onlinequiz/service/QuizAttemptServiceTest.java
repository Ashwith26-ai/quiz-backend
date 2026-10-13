package com.onlinequiz.service;

import com.onlinequiz.dto.QuestionAnswerSubmission;
import com.onlinequiz.dto.QuizSubmissionDto;
import com.onlinequiz.entity.*;
import com.onlinequiz.exception.QuizAlreadySubmittedException;
import com.onlinequiz.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository quizAttemptRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private OptionRepository optionRepository;
    @Mock
    private StudentAnswerRepository studentAnswerRepository;

    @InjectMocks
    private QuizAttemptServiceImpl quizAttemptService;

    private User student;
    private Quiz quiz;
    private Question q1;
    private Question q2;
    private Option opt1Correct;
    private Option opt1Wrong;
    private Option opt2Correct;
    private Option opt2Wrong;

    @BeforeEach
    void setUp() {
        student = new User("John Doe", "john@quiz.com", "pass", Role.ROLE_STUDENT);
        student.setId(10L);

        quiz = new Quiz("Java Basics", "Test your Java", 10, 2, 1);
        quiz.setId(100L);
        quiz.setActive(true);

        q1 = new Question(quiz, "What is JVM?", 1);
        q1.setId(1L);
        opt1Correct = new Option(q1, "Java Virtual Machine", true);
        opt1Correct.setId(11L);
        opt1Wrong = new Option(q1, "Java Variable Mode", false);
        opt1Wrong.setId(12L);
        q1.addOption(opt1Correct);
        q1.addOption(opt1Wrong);

        q2 = new Question(quiz, "Is Java object-oriented?", 1);
        q2.setId(2L);
        opt2Correct = new Option(q2, "Yes", true);
        opt2Correct.setId(21L);
        opt2Wrong = new Option(q2, "No", false);
        opt2Wrong.setId(22L);
        q2.addOption(opt2Correct);
        q2.addOption(opt2Wrong);
    }

    @Test
    void testStartQuizAttempt() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(student));
        when(quizRepository.findById(100L)).thenReturn(Optional.of(quiz));
        when(questionRepository.findByQuizId(100L)).thenReturn(List.of(q1, q2));
        when(quizAttemptRepository.findFirstByStudentIdAndQuizIdAndStatus(10L, 100L, AttemptStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());

        QuizAttempt attempt = new QuizAttempt(student, quiz, LocalDateTime.now());
        attempt.setId(500L);
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenReturn(attempt);

        QuizAttempt result = quizAttemptService.startQuizAttempt(10L, 100L);

        assertNotNull(result);
        assertEquals(500L, result.getId());
        assertEquals(AttemptStatus.IN_PROGRESS, result.getStatus());
        verify(quizAttemptRepository, times(1)).save(any(QuizAttempt.class));
    }

    @Test
    void testSubmitQuizCalculationAndPassStatus() {
        QuizAttempt inProgressAttempt = new QuizAttempt(student, quiz, LocalDateTime.now().minusMinutes(2));
        inProgressAttempt.setId(500L);

        when(quizAttemptRepository.findById(500L)).thenReturn(Optional.of(inProgressAttempt));
        when(questionRepository.findByQuizId(100L)).thenReturn(List.of(q1, q2));

        // Student chooses correct option for Q1 (opt1Correct) and skips Q2
        when(optionRepository.findById(11L)).thenReturn(Optional.of(opt1Correct));
        when(quizAttemptRepository.save(any(QuizAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        QuizSubmissionDto submissionDto = new QuizSubmissionDto(500L);
        List<QuestionAnswerSubmission> answers = new ArrayList<>();
        answers.add(new QuestionAnswerSubmission(1L, 11L)); // Correct
        // Q2 is unanswered / omitted
        submissionDto.setAnswers(answers);

        QuizAttempt result = quizAttemptService.submitQuiz(10L, submissionDto);

        assertNotNull(result);
        assertEquals(1, result.getScore()); // 1 out of 2
        assertEquals(50.0, result.getPercentage());
        assertEquals(AttemptStatus.PASSED, result.getStatus()); // passingMarks is 1
        assertNotNull(result.getSubmissionTime());
        verify(studentAnswerRepository, times(2)).save(any(StudentAnswer.class));
    }

    @Test
    void testSubmitQuizDuplicateSubmissionPrevention() {
        QuizAttempt completedAttempt = new QuizAttempt(student, quiz, LocalDateTime.now().minusMinutes(5));
        completedAttempt.setId(500L);
        completedAttempt.setStatus(AttemptStatus.PASSED);

        when(quizAttemptRepository.findById(500L)).thenReturn(Optional.of(completedAttempt));

        QuizSubmissionDto submissionDto = new QuizSubmissionDto(500L);

        assertThrows(QuizAlreadySubmittedException.class, () -> {
            quizAttemptService.submitQuiz(10L, submissionDto);
        });

        verify(studentAnswerRepository, never()).save(any(StudentAnswer.class));
    }
}
