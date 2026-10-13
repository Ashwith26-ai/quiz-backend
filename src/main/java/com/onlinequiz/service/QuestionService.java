package com.onlinequiz.service;

import com.onlinequiz.dto.QuestionFormDto;
import com.onlinequiz.dto.QuestionResponseDto;
import com.onlinequiz.entity.Question;

import java.util.List;

public interface QuestionService {
    List<Question> getQuestionsByQuizId(Long quizId);
    Question getQuestionById(Long id);
    Question createQuestion(QuestionFormDto formDto);
    Question updateQuestion(Long id, QuestionFormDto formDto);
    void deleteQuestion(Long id);
    QuestionFormDto getQuestionFormDto(Long id);
    List<QuestionResponseDto> getSanitizedQuestionsForQuiz(Long quizId);
    long countTotalQuestions();
}
