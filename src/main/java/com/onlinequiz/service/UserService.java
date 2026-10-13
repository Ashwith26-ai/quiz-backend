package com.onlinequiz.service;

import com.onlinequiz.dto.UserRegistrationDto;
import com.onlinequiz.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerStudent(UserRegistrationDto registrationDto);
    Optional<User> findByEmail(String email);
    List<User> getAllStudents();
    User getStudentById(Long id);
    long countStudents();
}
