package com.coworking.booking.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Глобальний обробник винятків для Booking Service.
 * Анотація @RestControllerAdvice забезпечує перехоплення винятків з усіх контролерів
 * та повернення уніфікованої структури ErrorResponse у форматі JSON.
 *
 * Ієрархія обробників (від специфічного до загального):
 * 1. ResourceNotFoundException → HTTP 404 Not Found
 * 2. IllegalArgumentException → HTTP 400 Bad Request
 * 3. IllegalStateException → HTTP 400 Bad Request
 * 4. MethodArgumentNotValidException → HTTP 400 Bad Request (помилки валідації)
 * 5. Exception (загальний обробник) → HTTP 500 Internal Server Error
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обробляє виняток відсутності ресурсу (HTTP 404 Not Found).
     * Виникає при зверненні до неіснуючого користувача, місця або бронювання.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Обробляє виняток некоректних аргументів (HTTP 400 Bad Request).
     * Виникає при некоректному часовому проміжку або дублюванні email.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Обробляє виняток некоректного стану об'єкта (HTTP 400 Bad Request).
     * Виникає при спробі забронювати зайняте місце або скасувати неактивне бронювання.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI()));
    }

    /**
     * Обробляє помилки валідації вхідних даних (HTTP 400 Bad Request).
     * Збирає повідомлення всіх полів з помилками у єдиний рядок через крапку з комою.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI()));
    }

    /**
     * Загальний обробник непередбачених винятків (HTTP 500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Внутрішня помилка сервера. Будь ласка, зверніться до адміністратора системи",
                        request.getRequestURI()));
    }

    private ErrorResponse buildError(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .message(message)
                .path(path)
                .build();
    }
}
