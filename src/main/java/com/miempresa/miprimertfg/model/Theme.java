package com.miempresa.miprimertfg.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del tema es obligatorio")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Theme(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
