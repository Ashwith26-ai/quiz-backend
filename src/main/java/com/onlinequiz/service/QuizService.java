package com.onlinequiz.service;

import com.onlinequiz.dto.QuizFormDto;
import com.onlinequiz.entity.Quiz;

import java.util.List;

public interface QuizService {
    List<Quiz> getAllQuizzes();
    List<Quiz> getActiveQuizzes();
    Quiz getQuizById(Long id);
    Quiz createQuiz(QuizFormDto formDto);
    Quiz updateQuiz(Long id, QuizFormDto formDto);
    void deleteQuiz(Long id);
    void toggleQuizStatus(Long id);
    long countQuizzes();
    long countActiveQuizzes();
    QuizFormDto getQuizFormDto(Long id);
}
