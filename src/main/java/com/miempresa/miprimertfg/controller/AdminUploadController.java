package com.miempresa.miprimertfg.controller;

import com.miempresa.miprimertfg.model.Question;
import com.miempresa.miprimertfg.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
public class AdminUploadController {

    private final FileUploadService fileUploadService;

    @GetMapping
    public String uploadPage(Model model) {
        return "admin/upload";
    }

    @PostMapping
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Por favor selecciona un archivo");
            return "redirect:/admin/upload";
        }

        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".json"))) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Formato no soportado. Use archivos CSV o JSON.");
            return "redirect:/admin/upload";
        }

        try {
            String createdBy = authentication != null ? authentication.getName() : "admin";
            List<Question> imported = fileUploadService.uploadQuestions(file, createdBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Importación exitosa! Se importaron " + imported.size() + " preguntas desde " + filename);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error al procesar el archivo: " + e.getMessage());
        }

        return "redirect:/admin/upload";
    }
}
