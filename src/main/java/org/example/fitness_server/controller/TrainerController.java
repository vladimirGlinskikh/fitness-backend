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
 * Контроллер REST API для управления тренерами фитнес-клуба.
 * <p>
 * Этот класс предоставляет набор эндпоинтов для выполнения CRUD-операций над сущностью {@code Trainer},
 * а также получения информации о текущем тренере, авторизованном в системе. Все операции используют
 * сервис {@code TrainerService} для бизнес-логики и репозиторий {@code TrainerRepository} для доступа
 * к данным. Контроллер интегрирован с Spring Security для обработки аутентификации.
 * </p>
 * <p>
 * Базовый путь для всех эндпоинтов: {@code /api/trainers}.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final TrainerRepository trainerRepository;

    /**
     * Возвращает список всех тренеров, зарегистрированных в системе.
     * <p>
     * Метод выполняет запрос к репозиторию для получения всех записей тренеров.
     * </p>
     *
     * @return список объектов {@code Trainer}
     */

    @GetMapping
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    /**
     * Возвращает информацию о тренере по указанному идентификатору.
     * <p>
     * Если тренер с заданным {@code id} не найден, возвращается ответ HTTP 404 (Not Found).
     * </p>
     *
     * @param id идентификатор тренера
     * @return {@code ResponseEntity} с объектом {@code Trainer} при успехе или HTTP 404 при отсутствии
     */

    @GetMapping("/{id}")
    public ResponseEntity<Trainer> getTrainerById(@PathVariable Long id) {
        return trainerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Создаёт нового тренера на основе предоставленных данных.
     * <p>
     * Метод вызывает сервис для выполнения бизнес-логики создания. В случае успеха возвращается
     * созданный объект {@code Trainer} со статусом HTTP 200. Если валидация данных не пройдена,
     * возвращается HTTP 400 с сообщением об ошибке. При других исключениях возвращается HTTP 500.
     * </p>
     *
     * @param trainer объект {@code Trainer} с данными нового тренера
     * @return {@code ResponseEntity} с созданным объектом {@code Trainer} или сообщением об ошибке
     */

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

    /**
     * Обновляет данные существующего тренера по указанному идентификатору.
     * <p>
     * Метод вызывает сервис для выполнения обновления. Если тренер с заданным {@code id} не найден
     * или данные не прошли валидацию, возвращается HTTP 400. При успехе возвращается обновлённый
     * объект {@code Trainer} со статусом HTTP 200.
     * </p>
     *
     * @param id идентификатор тренера для обновления
     * @param trainer объект {@code Trainer} с новыми данными
     * @return {@code ResponseEntity} с обновлённым объектом {@code Trainer} или HTTP 400 при ошибке
     */

    @PutMapping("/{id}")
    public ResponseEntity<Trainer> updateTrainer(@PathVariable Long id, @RequestBody Trainer trainer) {
        try {
            Trainer updatedTrainer = trainerService.updateTrainer(id, trainer);
            return ResponseEntity.ok(updatedTrainer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Удаляет тренера по указанному идентификатору.
     * <p>
     * Если тренер с заданным {@code id} существует, он удаляется, и возвращается HTTP 200.
     * Если тренер не найден, возвращается HTTP 404.
     * </p>
     *
     * @param id идентификатор тренера для удаления
     * @return {@code ResponseEntity} с HTTP 200 при успехе или HTTP 404 при отсутствии
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrainer(@PathVariable Long id) {
        if (trainerRepository.existsById(id)) {
            trainerRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Возвращает информацию о текущем авторизованном тренере.
     * <p>
     * Метод использует имя пользователя из объекта аутентификации для поиска тренера в репозитории.
     * Если тренер не найден, возвращается HTTP 404. Логирует запрос для отладки.
     * </p>
     *
     * @param authentication объект аутентификации, содержащий имя текущего пользователя
     * @return {@code ResponseEntity} с объектом {@code Trainer} при успехе или HTTP 404 при отсутствии
     */

    @GetMapping("/me")
    public ResponseEntity<Trainer> getCurrentTrainer(Authentication authentication) {
        System.out.println("Processing /api/trainers/me for user: " + authentication.getName());
        String username = authentication.getName();
        return trainerRepository.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}