package com.miempresa.miprimertfg.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Serves the React SPA at /react and /react/
 */
@Controller
public class SpaController {

    @GetMapping(value = { "/react", "/react/" }, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> reactApp() throws IOException {
        Resource resource = new ClassPathResource("static/react/index.html");
        byte[] content = resource.getInputStream().readAllBytes();
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(content);
    }
}
