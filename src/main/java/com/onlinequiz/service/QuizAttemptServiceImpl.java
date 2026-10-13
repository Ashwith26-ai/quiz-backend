package com.onlinequiz.service;

import com.onlinequiz.dto.DashboardStatsDto;
import com.onlinequiz.dto.QuestionAnswerSubmission;
import com.onlinequiz.dto.QuizSubmissionDto;
import com.onlinequiz.entity.*;
import com.onlinequiz.exception.QuizAlreadySubmittedException;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    public QuizAttemptServiceImpl(QuizAttemptRepository quizAttemptRepository,
                                  QuizRepository quizRepository,
                                  UserRepository userRepository,
                                  QuestionRepository questionRepository,
                                  OptionRepository optionRepository,
                                  StudentAnswerRepository studentAnswerRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.studentAnswerRepository = studentAnswerRepository;
    }

    @Override
    public QuizAttempt startQuizAttempt(Long studentId, Long quizId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        if (!Boolean.TRUE.equals(quiz.getActive())) {
            throw new IllegalArgumentException("This quiz is currently inactive and cannot be attempted.");
        }

        List<Question> questions = questionRepository.findByQuizId(quizId);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("This quiz has no questions yet and cannot be attempted.");
        }

        // Check if there is already an IN_PROGRESS attempt
        Optional<QuizAttempt> existingAttempt = quizAttemptRepository
                .findFirstByStudentIdAndQuizIdAndStatus(studentId, quizId, AttemptStatus.IN_PROGRESS);

        if (existingAttempt.isPresent()) {
            QuizAttempt attempt = existingAttempt.get();
            // Check if timer expired
            if (getRemainingTimeSeconds(attempt) <= 0) {
                // Auto-submit empty submission if already timed out
                QuizSubmissionDto emptyDto = new QuizSubmissionDto(attempt.getId());
                return submitQuiz(studentId, emptyDto);
            }
            return attempt;
        }

        QuizAttempt newAttempt = new QuizAttempt(student, quiz, LocalDateTime.now());
        return quizAttemptRepository.save(newAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizAttempt getAttemptById(Long attemptId) {
        return quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found with ID: " + attemptId));
    }

    @Override
    public QuizAttempt submitQuiz(Long studentId, QuizSubmissionDto submissionDto) {
        QuizAttempt attempt = getAttemptById(submissionDto.getAttemptId());

        if (!attempt.getStudent().getId().equals(studentId)) {
            throw new IllegalArgumentException("Unauthorized attempt submission.");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new QuizAlreadySubmittedException("This quiz attempt has already been submitted and scored.");
        }

        List<Question> questions = questionRepository.findByQuizId(attempt.getQuiz().getId());

        // Map submitted answers
        Map<Long, Long> answersMap = new HashMap<>();
        if (submissionDto.getAnswers() != null) {
            for (QuestionAnswerSubmission item : submissionDto.getAnswers()) {
                if (item.getQuestionId() != null && item.getSelectedOptionId() != null) {
                    answersMap.put(item.getQuestionId(), item.getSelectedOptionId());
                }
            }
        }

        int totalScore = 0;

        for (Question question : questions) {
            Long selectedOptionId = answersMap.get(question.getId());
            Option selectedOption = null;
            int marksAwarded = 0;

            if (selectedOptionId != null) {
                Optional<Option> opt = optionRepository.findById(selectedOptionId);
                if (opt.isPresent()) {
                    selectedOption = opt.get();
                    if (selectedOption.getQuestion().getId().equals(question.getId())
                            && Boolean.TRUE.equals(selectedOption.getCorrect())) {
                        marksAwarded = question.getMarks();
                        totalScore += marksAwarded;
                    }
                }
            }

            StudentAnswer studentAnswer = new StudentAnswer(attempt, question, selectedOption, marksAwarded);
            studentAnswerRepository.save(studentAnswer);
            attempt.addAnswer(studentAnswer);
        }

        int totalPossibleMarks = attempt.getQuiz().getTotalMarks();
        double percentage = totalPossibleMarks > 0 ? ((double) totalScore / totalPossibleMarks) * 100.0 : 0.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        AttemptStatus status = (totalScore >= attempt.getQuiz().getPassingMarks())
                ? AttemptStatus.PASSED
                : AttemptStatus.FAILED;

        attempt.setScore(totalScore);
        attempt.setPercentage(percentage);
        attempt.setStatus(status);
        attempt.setSubmissionTime(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getStudentAttempts(Long studentId) {
        return quizAttemptRepository.findByStudentIdOrderByStartTimeDesc(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAllAttempts() {
        return quizAttemptRepository.findAllByOrderByStartTimeDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getAttemptsByQuiz(Long quizId) {
        return quizAttemptRepository.findByQuizIdOrderByStartTimeDesc(quizId);
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStatistics() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalQuizzes(quizRepository.count());
        stats.setActiveQuizzes(quizRepository.findByActiveTrue().size());
        stats.setTotalQuestions(questionRepository.count());
        stats.setTotalStudents(userRepository.countByRole(Role.ROLE_STUDENT));

        long totalAttempts = quizAttemptRepository.count();
        long passedAttempts = quizAttemptRepository.countByStatus(AttemptStatus.PASSED);
        long failedAttempts = quizAttemptRepository.countByStatus(AttemptStatus.FAILED);

        stats.setTotalAttempts(totalAttempts);
        stats.setPassedAttempts(passedAttempts);
        stats.setFailedAttempts(failedAttempts);

        long completedAttempts = passedAttempts + failedAttempts;
        double passRate = completedAttempts > 0 ? ((double) passedAttempts / completedAttempts) * 100.0 : 0.0;
        stats.setPassRate(Math.round(passRate * 10.0) / 10.0);

        Double avgScore = quizAttemptRepository.getAveragePercentage();
        stats.setAverageScorePercentage(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public long getRemainingTimeSeconds(QuizAttempt attempt) {
        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            return 0;
        }
        LocalDateTime startTime = attempt.getStartTime();
        long durationSeconds = (long) attempt.getQuiz().getDurationMinutes() * 60;
        long elapsedSeconds = Duration.between(startTime, LocalDateTime.now()).getSeconds();
        long remaining = durationSeconds - elapsedSeconds;
        return Math.max(0, remaining);
    }
}
