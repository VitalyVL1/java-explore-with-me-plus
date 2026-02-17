package ru.practicum.dto.event;

/**
 * Составной DTO (Data Transfer Object) для передачи полных параметров обновления статусов заявок.
 * <p>
 * Используется для передачи в сервисный слой всей необходимой информации
 * для обновления статусов заявок на участие в событии. Объединяет идентификаторы
 * пользователя и события с DTO запроса на обновление.
 * </p>
 *
 * @param userId идентификатор пользователя-инициатора события
 * @param eventId идентификатор события, заявки которого обновляются
 * @param updateRequest DTO с множеством идентификаторов заявок и новым статусом
 * @see EventRequestStatusUpdateRequest
 */
public record EventRequestStatusUpdateRequestParam(
        Long userId,
        Long eventId,
        EventRequestStatusUpdateRequest updateRequest
) {
}