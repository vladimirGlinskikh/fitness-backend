package kz.zhelezyaka.fitness_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST-контроллеров приложения фитнес-клуба.
 * <p>
 * Этот класс перехватывает исключения типа {@code IllegalArgumentException},
 * возникающие в REST-контроллерах, и возвращает HTTP-ответ с кодом 400 (Bad Request),
 * содержащий сообщение об ошибке.
 * </p>
 *
 * @author Милана
 * @version 1.0
 * @since 2025-04-29
 */

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Конструктор по умолчанию.
     * <p>
     * Используется Spring для создания экземпляра класса.
     */
    public GlobalExceptionHandler() {
    }

    /**
     * Обрабатывает исключения типа {@code IllegalArgumentException}.
     * <p>
     * Формирует HTTP-ответ с кодом 400 (Bad Request) и телом, содержащим поля:
     * <ul>
     *     <li>{@code error}: фиксированное значение "Validation failed".</li>
     *     <li>{@code message}: текст сообщения исключения.</li>
     * </ul>
     *
     * @param ex исключение типа {@code IllegalArgumentException}
     * @return объект {@code ResponseEntity} с сообщением об ошибке
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Validation failed");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }
}
