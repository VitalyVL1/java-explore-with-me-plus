package ru.practicum.exception;

/**
 * Исключение, выбрасываемое при обращении к несуществующему ресурсу.
 * <p>
 * Используется в случаях, когда запрашиваемый объект не найден в базе данных.
 * Например:
 * <ul>
 *   <li>пользователь с указанным ID не существует</li>
 *   <li>событие с указанным ID не найдено</li>
 *   <li>категория с указанным ID отсутствует</li>
 *   <li>запрос на участие с указанным ID не существует</li>
 *   <li>комментарий с указанным ID не найден</li>
 * </ul>
 * </p>
 * <p>
 * Наследуется от {@link RuntimeException} и обрабатывается глобальным обработчиком
 * исключений {@link GlobalExceptionHandler} с возвратом HTTP статуса 404 (NOT_FOUND).
 * </p>
 *
 * @see GlobalExceptionHandler
 */
public class NotFoundException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение, описывающее причину исключения
     */
    public NotFoundException(String message) {
        super(message);
    }
}