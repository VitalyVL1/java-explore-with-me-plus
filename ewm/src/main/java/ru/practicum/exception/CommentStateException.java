package ru.practicum.exception;

/**
 * Исключение, выбрасываемое при некорректном статусе комментария.
 * <p>
 * Используется в случаях, когда операция с комментарием не может быть выполнена
 * из-за его текущего статуса. Например:
 * <ul>
 *   <li>попытка модерации комментария, который не находится в статусе WAITING</li>
 *   <li>попытка редактирования уже опубликованного комментария</li>
 *   <li>попытка публикации комментария с некорректным статусом</li>
 * </ul>
 * </p>
 * <p>
 * Наследуется от {@link RuntimeException} и обрабатывается глобальным обработчиком
 * исключений {@link GlobalExceptionHandler} с возвратом HTTP статуса 400 (BAD_REQUEST).
 * </p>
 *
 * @see ru.practicum.model.comment.CommentState
 * @see GlobalExceptionHandler
 */
public class CommentStateException extends RuntimeException {

    /**
     * Создает новое исключение с указанным сообщением об ошибке.
     *
     * @param message детальное сообщение, описывающее причину исключения
     */
    public CommentStateException(String message) {
        super(message);
    }
}