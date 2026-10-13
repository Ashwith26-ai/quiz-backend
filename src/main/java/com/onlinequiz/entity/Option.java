package com.onlinequiz.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 500)
    private String optionText;

    @Column(nullable = false)
    private Boolean isCorrect = false;

    public Option() {
    }

    public Option(Question question, String optionText, Boolean isCorrect) {
        this.question = question;
        this.optionText = optionText;
        this.isCorrect = isCorrect != null ? isCorrect : false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }
}
