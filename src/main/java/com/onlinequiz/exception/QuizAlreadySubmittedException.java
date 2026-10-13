package com.onlinequiz.exception;

public class QuizAlreadySubmittedException extends RuntimeException {
    public QuizAlreadySubmittedException(String message) {
        super(message);
    }
}
