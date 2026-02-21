package com.miempresa.miprimertfg.controller.api;

import com.miempresa.miprimertfg.dto.JwtResponse;
import com.miempresa.miprimertfg.dto.LoginRequest;
import com.miempresa.miprimertfg.dto.RegisterRequest;
import com.miempresa.miprimertfg.model.User;
import com.miempresa.miprimertfg.security.JwtTokenProvider;
import com.miempresa.miprimertfg.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "API de autenticación")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Operation(summary = "Iniciar sesión", description = "Autenticar usuario y obtener token JWT")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(userDetails);

            User user = userService.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Credenciales inválidas");
        }
    }

    @Operation(summary = "Registrar usuario", description = "Crear una nueva cuenta de usuario")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            User user = userService.register(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword());
            return ResponseEntity.ok("Usuario registrado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
