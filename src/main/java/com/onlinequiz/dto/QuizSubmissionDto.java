package com.onlinequiz.dto;

import java.util.ArrayList;
import java.util.List;

public class QuizSubmissionDto {

    private Long attemptId;
    private List<QuestionAnswerSubmission> answers = new ArrayList<>();

    public QuizSubmissionDto() {
    }

    public QuizSubmissionDto(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public List<QuestionAnswerSubmission> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QuestionAnswerSubmission> answers) {
        this.answers = answers;
    }
}
