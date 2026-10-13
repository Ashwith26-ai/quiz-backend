package com.onlinequiz.service;

import com.onlinequiz.dto.QuizFormDto;
import com.onlinequiz.entity.Quiz;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Quiz> getActiveQuizzes() {
        return quizRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + id));
    }

    @Override
    public Quiz createQuiz(QuizFormDto formDto) {
        Quiz quiz = new Quiz(
                formDto.getTitle().trim(),
                formDto.getDescription() != null ? formDto.getDescription().trim() : "",
                formDto.getDurationMinutes(),
                formDto.getTotalMarks(),
                formDto.getPassingMarks()
        );
        quiz.setActive(formDto.getActive() != null ? formDto.getActive() : true);
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(Long id, QuizFormDto formDto) {
        Quiz quiz = getQuizById(id);
        quiz.setTitle(formDto.getTitle().trim());
        quiz.setDescription(formDto.getDescription() != null ? formDto.getDescription().trim() : "");
        quiz.setDurationMinutes(formDto.getDurationMinutes());
        quiz.setTotalMarks(formDto.getTotalMarks());
        quiz.setPassingMarks(formDto.getPassingMarks());
        quiz.setActive(formDto.getActive() != null ? formDto.getActive() : true);
        return quizRepository.save(quiz);
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = getQuizById(id);
        quizRepository.delete(quiz);
    }

    @Override
    public void toggleQuizStatus(Long id) {
        Quiz quiz = getQuizById(id);
        quiz.setActive(!Boolean.TRUE.equals(quiz.getActive()));
        quizRepository.save(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public long countQuizzes() {
        return quizRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveQuizzes() {
        return quizRepository.findByActiveTrue().size();
    }

    @Override
    @Transactional(readOnly = true)
    public QuizFormDto getQuizFormDto(Long id) {
        Quiz quiz = getQuizById(id);
        QuizFormDto dto = new QuizFormDto();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDurationMinutes(quiz.getDurationMinutes());
        dto.setTotalMarks(quiz.getTotalMarks());
        dto.setPassingMarks(quiz.getPassingMarks());
        dto.setActive(quiz.getActive());
        return dto;
    }
}
