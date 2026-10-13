package com.onlinequiz.service;

import com.onlinequiz.dto.UserRegistrationDto;
import com.onlinequiz.entity.Role;
import com.onlinequiz.entity.User;
import com.onlinequiz.exception.ResourceNotFoundException;
import com.onlinequiz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerStudent(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("An account with email " + registrationDto.getEmail() + " already exists.");
        }

        User user = new User(
                registrationDto.getFullName().trim(),
                registrationDto.getEmail().trim().toLowerCase(),
                passwordEncoder.encode(registrationDto.getPassword()),
                Role.ROLE_STUDENT
        );

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email != null ? email.trim().toLowerCase() : null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllStudents() {
        return userRepository.findByRole(Role.ROLE_STUDENT);
    }

    @Override
    @Transactional(readOnly = true)
    public User getStudentById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public long countStudents() {
        return userRepository.countByRole(Role.ROLE_STUDENT);
    }
}
