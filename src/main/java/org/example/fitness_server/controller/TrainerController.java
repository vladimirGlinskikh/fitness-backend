package org.example.fitness_server.controller;

import org.example.fitness_server.model.Trainer;
import org.example.fitness_server.repository.TrainerRepository;
import org.example.fitness_server.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления тренерами в приложении фитнес-клуба.
 */
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerRepository trainerRepository;

    @GetMapping
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        return trainerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createTrainer(@RequestBody Trainer trainer) {
        try {
            Trainer createdTrainer = trainerService.createTrainer(trainer);
            return ResponseEntity.ok(createdTrainer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer trainer) {
        try {
            Trainer updatedTrainer = trainerService.updateTrainer(id, trainer);
            return ResponseEntity.ok(updatedTrainer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Trainer> getCurrentTrainer(Authentication authentication) {
        System.out.println("Processing /api/trainers/me for user: " + authentication.getName());
        String username = authentication.getName();
        return trainerRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}