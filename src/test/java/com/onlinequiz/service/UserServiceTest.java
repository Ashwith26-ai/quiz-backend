package com.onlinequiz.service;

import com.onlinequiz.dto.UserRegistrationDto;
import com.onlinequiz.entity.Role;
import com.onlinequiz.entity.User;
import com.onlinequiz.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setFullName("Test Student");
        registrationDto.setEmail("teststudent@quiz.com");
        registrationDto.setPassword("pass123");
        registrationDto.setConfirmPassword("pass123");
    }

    @Test
    void testRegisterStudentSuccess() {
        when(userRepository.existsByEmail("teststudent@quiz.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass123");

        User savedUser = new User("Test Student", "teststudent@quiz.com", "encoded_pass123", Role.ROLE_STUDENT);
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerStudent(registrationDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("teststudent@quiz.com", result.getEmail());
        assertEquals(Role.ROLE_STUDENT, result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testRegisterStudentDuplicateEmailThrowsException() {
        when(userRepository.existsByEmail("teststudent@quiz.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerStudent(registrationDto);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindByEmail() {
        User user = new User("Test Student", "teststudent@quiz.com", "encoded", Role.ROLE_STUDENT);
        when(userRepository.findByEmail("teststudent@quiz.com")).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByEmail("teststudent@quiz.com");
        assertTrue(found.isPresent());
        assertEquals("Test Student", found.get().getFullName());
    }
}
