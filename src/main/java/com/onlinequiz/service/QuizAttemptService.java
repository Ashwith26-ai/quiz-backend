package com.onlinequiz.service;

import com.onlinequiz.dto.DashboardStatsDto;
import com.onlinequiz.dto.QuizSubmissionDto;
import com.onlinequiz.entity.QuizAttempt;

import java.util.List;

public interface QuizAttemptService {
    QuizAttempt startQuizAttempt(Long studentId, Long quizId);
    QuizAttempt getAttemptById(Long attemptId);
    QuizAttempt submitQuiz(Long studentId, QuizSubmissionDto submissionDto);
    List<QuizAttempt> getStudentAttempts(Long studentId);
    List<QuizAttempt> getAllAttempts();
    List<QuizAttempt> getAttemptsByQuiz(Long quizId);
    DashboardStatsDto getDashboardStatistics();
    long getRemainingTimeSeconds(QuizAttempt attempt);
}
