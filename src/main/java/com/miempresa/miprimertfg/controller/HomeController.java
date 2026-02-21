package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.service.QuestionService;
import com.miempresa.miprimertfg.service.TestService;
import com.miempresa.miprimertfg.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QuestionService questionService;
    private final ThemeService themeService;
    private final TestService testService;

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Add statistics
        model.addAttribute("totalQuestions", questionService.countAll());
        model.addAttribute("totalThemes", themeService.findAll().size());

        // If user is authenticated, add user-specific stats
        if (userDetails != null) {
            // You would need to get userId from userDetails here
            // For now, we'll skip user-specific stats
            model.addAttribute("userTests", 0);
            model.addAttribute("avgScore", 0.0);
        }

        return "index";
    }
}
