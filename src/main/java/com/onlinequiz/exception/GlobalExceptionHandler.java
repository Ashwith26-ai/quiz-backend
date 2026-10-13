package com.onlinequiz.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", 404);
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleStaticNotFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("errorMessage", "The requested page was not found.");
        model.addAttribute("errorCode", 404);
        return "error/404";
    }

    @ExceptionHandler(QuizAlreadySubmittedException.class)
    public String handleQuizAlreadySubmitted(QuizAlreadySubmittedException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred.");
        model.addAttribute("errorCode", 500);
        return "error/500";
    }
}
