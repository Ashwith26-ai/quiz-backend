package com.onlinequiz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_answers")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "selected_option_id")
    private Option selectedOption;

    @Column(nullable = false)
    private Integer marksAwarded = 0;

    public StudentAnswer() {
    }

    public StudentAnswer(QuizAttempt attempt, Question question, Option selectedOption, Integer marksAwarded) {
        this.attempt = attempt;
        this.question = question;
        this.selectedOption = selectedOption;
        this.marksAwarded = marksAwarded != null ? marksAwarded : 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public void setAttempt(QuizAttempt attempt) {
        this.attempt = attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Option getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(Option selectedOption) {
        this.selectedOption = selectedOption;
    }

    public Integer getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(Integer marksAwarded) {
        this.marksAwarded = marksAwarded;
    }
}
