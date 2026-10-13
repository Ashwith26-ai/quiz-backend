package com.onlinequiz.service;

import com.onlinequiz.dto.OptionResponseDto;
import com.onlinequiz.dto.QuestionFormDto;
import com.onlinequiz.dto.QuestionResponseDto;
import com.onlinequiz.entity.Option;
import com.onlinequiz.entity.Question;
import com.onlinequiz.entity.Quiz;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.repository.OptionRepository;
import com.onlinequiz.repository.QuestionRepository;
import com.onlinequiz.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final OptionRepository optionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               QuizRepository quizRepository,
                               OptionRepository optionRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
        this.optionRepository = optionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Override
    @Transactional(readOnly = true)
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));
    }

    @Override
    public Question createQuestion(QuestionFormDto formDto) {
        Quiz quiz = quizRepository.findById(formDto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + formDto.getQuizId()));

        Question question = new Question(quiz, formDto.getQuestionText().trim(), formDto.getMarks());

        // Add 4 options
        String[] optionTexts = {
                formDto.getOption1().trim(),
                formDto.getOption2().trim(),
                formDto.getOption3().trim(),
                formDto.getOption4().trim()
        };

        for (int i = 0; i < optionTexts.length; i++) {
            boolean isCorrect = (formDto.getCorrectOption() != null && formDto.getCorrectOption() == (i + 1));
            Option option = new Option(question, optionTexts[i], isCorrect);
            question.addOption(option);
        }

        return questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Long id, QuestionFormDto formDto) {
        Question question = getQuestionById(id);
        question.setQuestionText(formDto.getQuestionText().trim());
        question.setMarks(formDto.getMarks());

        // Clear existing options and re-add updated options
        question.getOptions().clear();

        String[] optionTexts = {
                formDto.getOption1().trim(),
                formDto.getOption2().trim(),
                formDto.getOption3().trim(),
                formDto.getOption4().trim()
        };

        for (int i = 0; i < optionTexts.length; i++) {
            boolean isCorrect = (formDto.getCorrectOption() != null && formDto.getCorrectOption() == (i + 1));
            Option option = new Option(question, optionTexts[i], isCorrect);
            question.addOption(option);
        }

        return questionRepository.save(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        Question question = getQuestionById(id);
        questionRepository.delete(question);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionFormDto getQuestionFormDto(Long id) {
        Question question = getQuestionById(id);
        QuestionFormDto dto = new QuestionFormDto();
        dto.setId(question.getId());
        dto.setQuizId(question.getQuiz().getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setMarks(question.getMarks());

        List<Option> options = question.getOptions();
        if (options.size() >= 4) {
            dto.setOption1(options.get(0).getOptionText());
            dto.setOption2(options.get(1).getOptionText());
            dto.setOption3(options.get(2).getOptionText());
            dto.setOption4(options.get(3).getOptionText());

            for (int i = 0; i < 4; i++) {
                if (Boolean.TRUE.equals(options.get(i).getCorrect())) {
                    dto.setCorrectOption(i + 1);
                    break;
                }
            }
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getSanitizedQuestionsForQuiz(Long quizId) {
        List<Question> questions = questionRepository.findByQuizId(quizId);
        List<QuestionResponseDto> responseList = new ArrayList<>();

        for (Question q : questions) {
            QuestionResponseDto qDto = new QuestionResponseDto(
                    q.getId(),
                    q.getQuestionText(),
                    q.getMarks(),
                    q.getQuestionType()
            );

            List<OptionResponseDto> optionDtos = new ArrayList<>();
            for (Option opt : q.getOptions()) {
                // Notice: DO NOT expose isCorrect here!
                optionDtos.add(new OptionResponseDto(opt.getId(), opt.getOptionText()));
            }
            qDto.setOptions(optionDtos);
            responseList.add(qDto);
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalQuestions() {
        return questionRepository.count();
    }
}
