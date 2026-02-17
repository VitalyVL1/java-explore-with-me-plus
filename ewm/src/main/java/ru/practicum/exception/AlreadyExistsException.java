package ru.practicum.exception;

/**
 * Исключение, выбрасываемое при попытке создания ресурса, который уже существует.
 * <p>
 * Используется в случаях, когда нарушается уникальность данных:
 * пользователь с таким email уже зарегистрирован, категория с таким именем уже существует,
 * подборка с таким заголовком уже создана и т.д.
 * </p>
 * <p>
 * Наследуется от {@link RuntimeException} и обрабатывается глобальным обработчиком
 * исключений {@link GlobalExceptionHandler} с возвратом HTTP статуса 409 (CONFLICT).
 * </p>
 *
 * @see GlobalExceptionHandler
 * @see org.springframework.dao.DataIntegrityViolationException
 */
public class AlreadyExistsException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение, описывающее причину исключения
     */
    public AlreadyExistsException(String message) {
        super(message);
    }
}