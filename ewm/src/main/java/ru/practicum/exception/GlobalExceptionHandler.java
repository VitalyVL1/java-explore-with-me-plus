package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

/**
 * Глобальный обработчик исключений для всех REST-контроллеров приложения.
 * <p>
 * Перехватывает различные типы исключений, возникающих в процессе обработки запросов,
 * и преобразует их в стандартизированный ответ {@link ApiError} с соответствующим
 * HTTP статусом. Все ошибки логируются с уровнем WARN или ERROR.
 * </p>
 *
 * @see ApiError
 * @see HttpStatus
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение при отсутствии обязательного параметра запроса.
     *
     * @param e исключение {@link MissingServletRequestParameterException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Ожидался обязательный параметр", e.getMessage());
    }

    /**
     * Обрабатывает исключение при несоответствии типа переданного параметра.
     *
     * @param e исключение {@link MethodArgumentTypeMismatchException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Несоответствие типов параметров", e.getMessage());
    }

    /**
     * Обрабатывает исключения при нарушении валидации аргументов метода контроллера.
     * Собирает все ошибки валидации полей и включает их в ответ.
     *
     * @param e исключение {@link MethodArgumentNotValidException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST) и списком ошибок валидации
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("400 {}", e.getMessage(), e);
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .toList();
        return new ApiError("BAD_REQUEST", "Переданные в метод контроллера данные не проходят проверку на валидацию",
                e.getMessage(), errors);
    }

    /**
     * Обрабатывает исключение, связанное с некорректным статусом комментария.
     *
     * @param e исключение {@link CommentStateException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCommentStateException(final CommentStateException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Несоответствие статуса комментария", e.getMessage());
    }

    /**
     * Обрабатывает исключение при обращении к несуществующему ресурсу.
     *
     * @param e исключение {@link NotFoundException}
     * @return объект {@link ApiError} с HTTP статусом 404 (NOT_FOUND)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.warn("404 {}", e.getMessage(), e);
        return new ApiError("NOT_FOUND", "Обращение к несуществующему ресурсу", e.getMessage());
    }

    /**
     * Обрабатывает исключения, связанные с нарушением целостности данных
     * или уникальности ограничений в базе данных.
     *
     * @param e исключение {@link AlreadyExistsException} или {@link DataIntegrityViolationException}
     * @return объект {@link ApiError} с HTTP статусом 409 (CONFLICT)
     */
    @ExceptionHandler({AlreadyExistsException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityException(final Exception e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError("CONFLICT", "Нарушение ограничения уникальности", e.getMessage());
    }

    /**
     * Обрабатывает исключение при нарушении бизнес-условий операции.
     *
     * @param e исключение {@link ConditionsNotMetException}
     * @return объект {@link ApiError} с HTTP статусом 409 (CONFLICT)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final ConditionsNotMetException e) {
        log.warn("409 {}", e.getMessage(), e);
        return new ApiError("CONFLICT", "Нарушение ограничений", e.getMessage());
    }

    /**
     * Обрабатывает исключение при ошибке валидации данных.
     *
     * @param e исключение {@link ValidationException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Переданные в метод контроллера данные не проходят проверку на валидацию",
                e.getMessage());
    }

    /**
     * Обрабатывает исключение при передаче некорректного аргумента.
     *
     * @param e исключение {@link IllegalArgumentException}
     * @return объект {@link ApiError} с HTTP статусом 400 (BAD_REQUEST)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("400 {}", e.getMessage(), e);
        return new ApiError("BAD_REQUEST", "Передан неправильный аргумент", e.getMessage());
    }

    /**
     * Обрабатывает исключение при попытке доступа без необходимых прав.
     *
     * @param e исключение {@link AccessDeniedException}
     * @return объект {@link ApiError} с HTTP статусом 403 (FORBIDDEN)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleAccessDeniedException(final AccessDeniedException e) {
        log.warn("403 {}", e.getMessage(), e);
        return new ApiError("FORBIDDEN", "Доступ к этой операции запрещён", e.getMessage());
    }

    /**
     * Обрабатывает все непредвиденные исключения.
     *
     * @param e общее исключение {@link Exception}
     * @return объект {@link ApiError} с HTTP статусом 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.warn("500 {}", e.getMessage(), e);
        return new ApiError("INTERNAL_SERVER_ERROR", "На сервере произошла внутренняя ошибка",
                e.getMessage());
    }
}