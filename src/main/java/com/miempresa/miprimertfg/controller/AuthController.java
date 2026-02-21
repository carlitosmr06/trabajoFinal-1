package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (result.hasErrors()) {
            return "register";
        }

        try {
            // Check if username already exists
            if (userService.existsByUsername(user.getUsername())) {
                model.addAttribute("error", "El nombre de usuario ya está en uso");
                return "register";
            }

            // Check if email already exists
            if (userService.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "El email ya está registrado");
                return "register";
            }

            // Register the user
            userService.registerUser(user);

            redirectAttributes.addFlashAttribute("success", "Registro exitoso. Por favor, inicia sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());
            return "register";
        }
    }
}
