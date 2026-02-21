package com.miempresa.miprimertfg.service;

import com.miempresa.miprimertfg.model.Theme;
import com.miempresa.miprimertfg.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ThemeService {

    private final ThemeRepository themeRepository;

    public Theme save(Theme theme) {
        return themeRepository.save(theme);
    }

    public Optional<Theme> findById(Long id) {
        return themeRepository.findById(id);
    }

    public Optional<Theme> findByName(String name) {
        return themeRepository.findByName(name);
    }

    public List<Theme> findAll() {
        return themeRepository.findAll();
    }

    public void deleteById(Long id) {
        themeRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return themeRepository.existsByName(name);
    }

    public Theme findOrCreate(String name) {
        return themeRepository.findByName(name)
                .orElseGet(() -> themeRepository.save(new Theme(name, "")));
    }
}
