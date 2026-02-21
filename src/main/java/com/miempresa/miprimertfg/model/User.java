package com.miempresa.miprimertfg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private List<String> roles = new ArrayList<>();

    @Column(nullable = false)
    private Boolean enabled = true;

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles.add("ROLE_USER");
    }
}
