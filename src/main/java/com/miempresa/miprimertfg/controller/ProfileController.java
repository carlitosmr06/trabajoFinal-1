package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.TestResult;
import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.service.TestService;
import com.miempresa.miprimertfg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

        private final UserService userService;
        private final TestService testService;

        @GetMapping
        public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
                System.out.println("\n=== LOADING PROFILE PAGE ===");
                System.out.println("User: " + userDetails.getUsername());

                // Get current user
                User user = userService.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                // Load test results for statistics
                List<TestResult> allResults = testService.getRecentUserResults(user);

                // Calculate statistics
                int totalTests = allResults.size();
                double averageScore = allResults.stream()
                                .mapToDouble(TestResult::getScore)
                                .average()
                                .orElse(0.0);

                int totalQuestions = allResults.stream()
                                .mapToInt(TestResult::getTotalQuestions)
                                .sum();

                System.out.println("Total tests: " + totalTests);
                System.out.println("Average score: " + averageScore + "%");
                System.out.println("Total questions answered: " + totalQuestions);
                System.out.println("=== PROFILE PAGE LOADED ===\n");

                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("roles", userDetails.getAuthorities());
                model.addAttribute("totalTests", totalTests);
                model.addAttribute("averageScore", String.format("%.1f", averageScore));
                model.addAttribute("totalQuestions", totalQuestions);

                return "profile/profile";
        }

        @GetMapping("/results")
        public String results(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        Model model) {

                System.out.println("\n=== LOADING PROFILE RESULTS ===");
                System.out.println("User: " + userDetails.getUsername());

                // Get current user
                User user = userService.findByUsername(userDetails.getUsername())
                                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

                System.out.println("User ID: " + user.getId());

                // Load test results with pagination
                Pageable pageable = PageRequest.of(page, size, Sort.by("completedAt").descending());
                Page<TestResult> resultsPage = testService.getUserResults(user, pageable);

                System.out.println("Total results found: " + resultsPage.getTotalElements());
                System.out.println("Results on this page: " + resultsPage.getNumberOfElements());

                // Calculate statistics
                List<TestResult> allResults = testService.getRecentUserResults(user);
                double averageScore = allResults.stream()
                                .mapToDouble(TestResult::getScore)
                                .average()
                                .orElse(0.0);

                double bestScore = allResults.stream()
                                .mapToDouble(TestResult::getScore)
                                .max()
                                .orElse(0.0);

                System.out.println("Average score: " + averageScore + "%");
                System.out.println("Best score: " + bestScore + "%");
                System.out.println("=== PROFILE RESULTS LOADED ===\n");

                model.addAttribute("username", userDetails.getUsername());
                model.addAttribute("resultsPage", resultsPage);
                model.addAttribute("totalTests", allResults.size());
                model.addAttribute("averageScore", String.format("%.1f", averageScore));
                model.addAttribute("bestScore", String.format("%.1f", bestScore));

                return "profile/results";
        }
}
