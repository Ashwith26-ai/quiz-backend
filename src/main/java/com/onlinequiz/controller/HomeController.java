package com.onlinequiz.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("userEmail", auth.getName());
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
