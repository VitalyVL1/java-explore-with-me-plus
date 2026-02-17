package ru.practicum.dto.event;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

/**
 * DTO (Data Transfer Object) для результата обновления статусов заявок на участие.
 * <p>
 * Используется для возврата клиенту информации о результате обработки запроса
 * на обновление статусов заявок. Содержит раздельные списки подтвержденных
 * и отклоненных заявок с их полными данными.
 * </p>
 *
 * @param confirmedRequests список DTO заявок, которые были подтверждены
 * @param rejectedRequests список DTO заявок, которые были отклонены
 * @see ParticipationRequestDto
 */
public record EventRequestStatusUpdateResult(
        List<ParticipationRequestDto> confirmedRequests,
        List<ParticipationRequestDto> rejectedRequests
) {
}