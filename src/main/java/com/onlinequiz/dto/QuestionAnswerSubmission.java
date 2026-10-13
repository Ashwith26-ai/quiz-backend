package com.onlinequiz.dto;

public class QuestionAnswerSubmission {
    private Long questionId;
    private Long selectedOptionId;

    public QuestionAnswerSubmission() {
    }

    public QuestionAnswerSubmission(Long questionId, Long selectedOptionId) {
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
