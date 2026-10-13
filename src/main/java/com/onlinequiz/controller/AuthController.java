package com.onlinequiz.controller;

import com.onlinequiz.dto.UserRegistrationDto;
import com.onlinequiz.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "registered", required = false) String registered,
                            Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            return isAdmin ? "redirect:/admin/dashboard" : "redirect:/student/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("successMessage", "Registration successful! You can now log in with your credentials.");
        }

        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "redirect:/student/dashboard";
        }
        model.addAttribute("registrationDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registrationDto") UserRegistrationDto registrationDto,
                               BindingResult bindingResult,
                               Model model) {
        if (!registrationDto.isPasswordMatching()) {
            bindingResult.rejectValue("confirmPassword", "error.registrationDto", "Passwords do not match.");
        }

        if (userService.findByEmail(registrationDto.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.registrationDto", "Email address is already registered.");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerStudent(registrationDto);
            return "redirect:/login?registered=true";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }
}
