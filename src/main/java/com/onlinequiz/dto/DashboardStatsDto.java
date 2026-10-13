package com.onlinequiz.dto;

public class DashboardStatsDto {
    private long totalQuizzes;
    private long activeQuizzes;
    private long totalQuestions;
    private long totalStudents;
    private long totalAttempts;
    private long passedAttempts;
    private long failedAttempts;
    private double passRate;
    private double averageScorePercentage;

    public DashboardStatsDto() {
    }

    public long getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(long totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public long getActiveQuizzes() {
        return activeQuizzes;
    }

    public void setActiveQuizzes(long activeQuizzes) {
        this.activeQuizzes = activeQuizzes;
    }

    public long getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(long totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getPassedAttempts() {
        return passedAttempts;
    }

    public void setPassedAttempts(long passedAttempts) {
        this.passedAttempts = passedAttempts;
    }

    public long getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(long failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public double getPassRate() {
        return passRate;
    }

    public void setPassRate(double passRate) {
        this.passRate = passRate;
    }

    public double getAverageScorePercentage() {
        return averageScorePercentage;
    }

    public void setAverageScorePercentage(double averageScorePercentage) {
        this.averageScorePercentage = averageScorePercentage;
    }
}
