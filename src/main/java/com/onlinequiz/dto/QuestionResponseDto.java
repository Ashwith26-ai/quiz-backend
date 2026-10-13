package com.onlinequiz.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionResponseDto {
    private Long id;
    private String questionText;
    private Integer marks;
    private String questionType;
    private List<OptionResponseDto> options = new ArrayList<>();

    public QuestionResponseDto() {
    }

    public QuestionResponseDto(Long id, String questionText, Integer marks, String questionType) {
        this.id = id;
        this.questionText = questionText;
        this.marks = marks;
        this.questionType = questionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public List<OptionResponseDto> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResponseDto> options) {
        this.options = options;
    }
}
