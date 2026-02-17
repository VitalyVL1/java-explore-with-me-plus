package ru.practicum.dto.event;

/**
 * Составной DTO (Data Transfer Object) для передачи полных параметров обновления события пользователем.
 * <p>
 * Используется для передачи в сервисный слой всей необходимой информации
 * для обновления события пользователем. Объединяет идентификаторы пользователя
 * и события с DTO запроса на обновление.
 * </p>
 *
 * @param userId идентификатор пользователя-инициатора события
 * @param eventId идентификатор обновляемого события
 * @param request DTO с данными для обновления события
 * @see UpdateEventUserRequest
 */
public record UpdateEventUserRequestParam(
        Long userId,
        Long eventId,
        UpdateEventUserRequest request
) {
}