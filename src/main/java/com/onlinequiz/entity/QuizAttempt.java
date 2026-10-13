package com.onlinequiz.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime submissionTime;

    @Column(nullable = false)
    private Integer score = 0;

    @Column(nullable = false)
    private Double percentage = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttemptStatus status = AttemptStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudentAnswer> answers = new ArrayList<>();

    public QuizAttempt() {
    }

    public QuizAttempt(User student, Quiz quiz, LocalDateTime startTime) {
        this.student = student;
        this.quiz = quiz;
        this.startTime = startTime;
        this.status = AttemptStatus.IN_PROGRESS;
        this.score = 0;
        this.percentage = 0.0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(LocalDateTime submissionTime) {
        this.submissionTime = submissionTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public void setStatus(AttemptStatus status) {
        this.status = status;
    }

    public List<StudentAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StudentAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswer(StudentAnswer answer) {
        answers.add(answer);
        answer.setAttempt(this);
    }
}
