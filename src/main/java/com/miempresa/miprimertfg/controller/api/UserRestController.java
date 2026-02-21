package com.miempresa.miprimertfg.controller.api;

import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "API de gestión de usuarios")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserRestController {

    private final UserService userService;

    @Data
    public static class UpdateProfileRequest {
        private String email;
    }

    @Data
    public static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
    }

    @Operation(summary = "Obtener perfil", description = "Obtener perfil del usuario autenticado")
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Remove password from response
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar perfil", description = "Actualizar perfil del usuario autenticado")
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            User updated = userService.updateProfile(user.getId(), request.getEmail());
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Cambiar contraseña", description = "Cambiar contraseña del usuario")
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            userService.changePassword(user.getId(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Contraseña actualizada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Listar usuarios", description = "Listar todos los usuarios (requiere rol ADMIN)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> users = userService.findAll(pageable);
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar usuario por ID", description = "Actualizar email de un usuario por ID (requiere rol ADMIN)")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserById(
            @PathVariable Long id,
            @RequestBody UpdateProfileRequest request) {
        try {
            User updated = userService.updateProfile(id, request.getEmail());
            updated.setPassword(null);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Eliminar un usuario por ID (requiere rol ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
