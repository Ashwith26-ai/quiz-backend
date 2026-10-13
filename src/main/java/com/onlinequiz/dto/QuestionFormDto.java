package com.onlinequiz.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionFormDto {

    private Long id;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Marks is required")
    @Min(value = 1, message = "Marks must be at least 1")
    private Integer marks = 1;

    @NotBlank(message = "Option A is required")
    private String option1;

    @NotBlank(message = "Option B is required")
    private String option2;

    @NotBlank(message = "Option C is required")
    private String option3;

    @NotBlank(message = "Option D is required")
    private String option4;

    @NotNull(message = "Please select the correct option")
    private Integer correctOption; // 1, 2, 3, or 4

    public QuestionFormDto() {
    }

    public QuestionFormDto(Long quizId) {
        this.quizId = quizId;
        this.marks = 1;
        this.correctOption = 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
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

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }

    public Integer getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(Integer correctOption) {
        this.correctOption = correctOption;
    }
}
