package ru.practicum.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.model.request.RequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для представления заявки на участие в событии.
 * <p>
 * Используется для передачи информации о заявке на участие между клиентом и сервером.
 * Содержит данные о статусе заявки, времени создания, идентификаторах события и заявителя.
 * </p>
 *
 * @see RequestStatus
 */
@Data
@Builder
public class ParticipationRequestDto {
    /**
     * Уникальный идентификатор заявки.
     */
    private Long id;

    /**
     * Статус заявки {@link RequestStatus}:
     * PENDING - ожидает подтверждения,
     * CONFIRMED - подтверждена,
     * REJECTED - отклонена,
     * CANCELED - отменена.
     */
    private RequestStatus status;

    /**
     * Дата и время создания заявки.
     * Форматируется согласно шаблону {@link ru.practicum.util.DateTimeFormat#DATE_TIME_PATTERN}.
     */
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime created;

    /**
     * Идентификатор события, на которое подана заявка.
     * В JSON сериализуется под именем "event".
     */
    @JsonProperty("event")
    private Long eventId;

    /**
     * Идентификатор пользователя, подавшего заявку.
     * В JSON сериализуется под именем "requester".
     */
    @JsonProperty("requester")
    private Long requesterId;
}