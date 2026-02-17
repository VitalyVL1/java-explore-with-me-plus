package ru.practicum.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Глобальный обработчик исключений для всех REST-контроллеров сервиса статистики.
 * <p>
 * Перехватывает различные типы исключений, возникающих в процессе обработки запросов,
 * и преобразует их в стандартизированный ответ в формате JSON с соответствующим
 * HTTP статусом. Все ошибки логируются с уровнем WARN.
 * </p>
 *
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения при нарушении валидации аргументов метода контроллера
     * (например, при использовании аннотации {@link jakarta.validation.Valid}).
     *
     * @param e исключение {@link MethodArgumentNotValidException}
     * @return карта с ключом "error" и сообщением об ошибке, HTTP статус 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .findFirst().filter(error -> error.getDefaultMessage() != null)
                .map(DefaultMessageSourceResolvable::getDefaultMessage).orElse("Ошибка валидации");
        return Map.of("error", errorMessage);
    }

    /**
     * Обрабатывает исключения при нарушении ограничений валидации
     * (например, при использовании аннотаций валидации на параметрах методов).
     *
     * @param e исключение {@link ConstraintViolationException}
     * @return карта с ключом "error" и сообщением об ошибке, HTTP статус 400 (BAD_REQUEST)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(final ConstraintViolationException e) {
        log.warn("ConstraintViolationException: {}", e.getMessage());
        String errorMessage = e.getConstraintViolations().stream()
                .findFirst().filter(violation -> violation.getMessage() != null)
                .map(ConstraintViolation::getMessage).orElse("Нарушение ограничения");
        return Map.of("error", errorMessage);
    }

    /**
     * Обрабатывает исключения при несоответствии типа переданного параметра.
     *
     * @param e исключение {@link MethodArgumentTypeMismatchException}
     * @return карта с ключом "error" и сообщением об ошибке, HTTP статус 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());
        return Map.of("error", "Параметр '" + e.getName() + "' должен быть типа " +
                               (e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "число"));
    }

    /**
     * Обрабатывает исключения при отсутствии обязательного параметра запроса.
     *
     * @param e исключение {@link MissingServletRequestParameterException}
     * @return карта с ключом "error" и сообщением об ошибке, HTTP статус 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("MissingServletRequestParameterException: {}", e.getMessage());
        return Map.of("error", "Отсутствует обязательный параметр: " + e.getParameterName());
    }

    /**
     * Обрабатывает исключения валидации бизнес-логики.
     *
     * @param e исключение {@link ValidationException}
     * @return карта с ключом "error" и сообщением об ошибке, HTTP статус 400 (BAD_REQUEST)
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        log.warn("ValidationException: {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }

    /**
     * Обрабатывает все непредвиденные исключения.
     * <p>
     * Возвращает общее сообщение о внутренней ошибке сервера, скрывая детали исключения
     * от клиента в целях безопасности.
     * </p>
     *
     * @param e общее исключение {@link Exception}
     * @return карта с ключами "error" и "message", HTTP статус 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Exception e) {
        log.warn("Внутренняя ошибка: {}", e.getMessage(), e);
        return Map.of(
                "error", "Внутренняя ошибка сервера",
                "message", "Произошла непредвиденная ошибка"
        );
    }
}