package com.onlinequiz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(nullable = false)
    private Integer marks = 1;

    @Column(nullable = false, length = 50)
    private String questionType = "MULTIPLE_CHOICE";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Option> options = new ArrayList<>();

    public Question() {
    }

    public Question(Quiz quiz, String questionText, Integer marks) {
        this.quiz = quiz;
        this.questionText = questionText;
        this.marks = marks != null ? marks : 1;
        this.questionType = "MULTIPLE_CHOICE";
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public void addOption(Option option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void removeOption(Option option) {
        options.remove(option);
        option.setQuestion(null);
    }

    public Option getCorrectOption() {
        if (options == null) return null;
        for (Option opt : options) {
            if (Boolean.TRUE.equals(opt.getCorrect())) {
                return opt;
            }
        }
        return null;
    }
}
