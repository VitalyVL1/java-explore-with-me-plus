package ru.practicum.exception;

/**
 * Исключение, выбрасываемое при попытке доступа к ресурсу без необходимых прав.
 * <p>
 * Используется в случаях, когда пользователь пытается выполнить операцию,
 * на которую у него нет прав (например, редактирование чужого события,
 * просмотр приватных данных другого пользователя и т.д.).
 * </p>
 * <p>
 * Наследуется от {@link RuntimeException} и может быть перехвачено
 * глобальным обработчиком исключений для возврата HTTP статуса 403 (Forbidden).
 * </p>
 *
 * @see GlobalExceptionHandler
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение, описывающее причину исключения
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}