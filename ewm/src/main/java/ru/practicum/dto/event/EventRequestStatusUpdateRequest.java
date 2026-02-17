package ru.practicum.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import ru.practicum.model.request.RequestStatus;

import java.util.Set;

/**
 * DTO (Data Transfer Object) для запроса на обновление статусов заявок на участие в событии.
 * <p>
 * Используется инициатором события для подтверждения или отклонения заявок других пользователей.
 * Содержит множество идентификаторов заявок и статус, который нужно им присвоить.
 * </p>
 *
 * @param requestIds множество идентификаторов заявок, статус которых требуется обновить
 * @param status новый статус для указанных заявок (должен быть CONFIRMED или REJECTED)
 * @see RequestStatus
 */
public record EventRequestStatusUpdateRequest(
        @NotNull(message = "Заявки для обновления должны быть указаны")
        Set<Long> requestIds,
        @NotNull(message = "Статус для обновления должен быть указан")
        RequestStatus status
) {
    /**
     * Валидирует допустимость запрашиваемого статуса.
     * <p>
     * Проверяет, что запрашиваемый статус является одним из допустимых для операции:
     * CONFIRMED (подтвердить заявку) или REJECTED (отклонить заявку).
     * Статусы PENDING и CANCELED недопустимы для этой операции.
     * </p>
     *
     * @return true если статус является CONFIRMED или REJECTED
     */
    @AssertTrue(message = "Пользователь может использовать только CONFIRMED или REJECTED")
    public boolean isValidRequestStatus() {
        return status == RequestStatus.CONFIRMED ||
               status == RequestStatus.REJECTED;
    }
}