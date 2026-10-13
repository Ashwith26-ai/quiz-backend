package com.onlinequiz.controller;

import com.onlinequiz.dto.UserRegistrationDto;
import com.onlinequiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testLoginPageAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegisterPageAccessible() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationDto"));
    }

    @Test
    void testRegisterSuccessRedirectsToLogin() throws Exception {
        when(userService.findByEmail("newstudent@quiz.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Alex Hunter")
                        .param("email", "newstudent@quiz.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));
    }

    @Test
    void testRegisterPasswordMismatchShowsErrors() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("fullName", "Alex Hunter")
                        .param("email", "newstudent@quiz.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "differentPass"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }
}
