package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для стандартизированного представления ошибок API.
 * <p>
 * Используется глобальным обработчиком исключений {@link GlobalExceptionHandler}
 * для формирования единообразного ответа при возникновении ошибок.
 * Поля с null значениями исключаются из JSON-ответа.
 * </p>
 *
 * @param status HTTP статус ошибки (например, "BAD_REQUEST", "NOT_FOUND")
 * @param reason краткое описание типа ошибки
 * @param message детальное сообщение об ошибке
 * @param timestamp время возникновения ошибки
 * @param errors список дополнительных ошибок (например, при валидации нескольких полей)
 *
 * @see GlobalExceptionHandler
 * @see JsonInclude
 * @see JsonFormat
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String status,
        String reason,
        String message,
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime timestamp,
        List<String> errors
) {
    /**
     * Создает объект ошибки без списка дополнительных ошибок.
     * Время ошибки устанавливается на текущий момент.
     *
     * @param status HTTP статус ошибки
     * @param reason краткое описание типа ошибки
     * @param message детальное сообщение об ошибке
     */
    public ApiError(String status, String reason, String message) {
        this(status, reason, message, LocalDateTime.now(), null);
    }

    /**
     * Создает объект ошибки со списком дополнительных ошибок.
     * Время ошибки устанавливается на текущий момент.
     *
     * @param status HTTP статус ошибки
     * @param reason краткое описание типа ошибки
     * @param message детальное сообщение об ошибке
     * @param errors список дополнительных ошибок (например, ошибки валидации полей)
     */
    public ApiError(String status, String reason, String message, List<String> errors) {
        this(status, reason, message, LocalDateTime.now(), errors);
    }
}