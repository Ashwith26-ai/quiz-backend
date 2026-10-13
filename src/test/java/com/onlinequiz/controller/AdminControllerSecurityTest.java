package com.onlinequiz.controller;

import com.onlinequiz.dto.DashboardStatsDto;
import com.onlinequiz.entity.Role;
import com.onlinequiz.entity.User;
import com.onlinequiz.service.QuizAttemptService;
import com.onlinequiz.service.QuizService;
import com.onlinequiz.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private QuizService quizService;

    @MockBean
    private QuizAttemptService quizAttemptService;

    @Test
    void testAnonymousUserCannotAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "student@quiz.com", roles = {"STUDENT"})
    void testStudentCannotAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@quiz.com", roles = {"ADMIN"})
    void testAdminCanAccessAdminDashboard() throws Exception {
        User admin = new User("System Admin", "admin@quiz.com", "pass", Role.ROLE_ADMIN);
        when(userService.findByEmail("admin@quiz.com")).thenReturn(Optional.of(admin));
        when(quizAttemptService.getDashboardStatistics()).thenReturn(new DashboardStatsDto());
        when(quizAttemptService.getAllAttempts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("stats"));
    }
}
