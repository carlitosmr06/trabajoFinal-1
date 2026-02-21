package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findAll(pageable);
        users.forEach(u -> u.setPassword(null));

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("totalUsers", users.getTotalElements());

        return "admin/users";
    }

    @GetMapping("/{id}/edit")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("user", user);
        return "admin/user-form";
    }

    @PostMapping("/{id}/edit")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            user.setUsername(username);

            // Validar si el email existe en otro usuario
            userService.findByEmail(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("El email ya está uso por otro usuario");
                }
            });
            user.setEmail(email);

            // Actualizar rol
            user.setRoles(new java.util.ArrayList<>(java.util.Collections.singletonList(role)));

            // Actualizar password si no está vacío
            if (password != null && !password.isBlank()) {
                user.setPassword(passwordEncoder.encode(password));
            }

            userService.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "Usuario actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
