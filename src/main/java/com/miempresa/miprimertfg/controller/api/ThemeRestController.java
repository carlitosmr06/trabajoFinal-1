package com.miempresa.miprimertfg.controller.api;

import com.miempresa.miprimertfg.model.Theme;
import com.miempresa.miprimertfg.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Themes", description = "API de temas de preguntas")
@RestController
@RequestMapping("/api/themes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ThemeRestController {

    private final ThemeService themeService;

    @Operation(summary = "Listar temas", description = "Obtener todos los temas disponibles")
    @GetMapping
    public ResponseEntity<List<Theme>> getAllThemes() {
        return ResponseEntity.ok(themeService.findAll());
    }
}
