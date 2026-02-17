package ru.practicum.dto.request;

import ru.practicum.model.request.Request;

import java.util.List;

/**
 * Утилитарный класс-маппер для преобразования между сущностью запроса на участие и DTO.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link Request}
 * в объект {@link ParticipationRequestDto}. Использует паттерн Builder для создания DTO.
 * Поддерживает преобразование как одиночных объектов, так и списков.
 * </p>
 *
 * @see Request
 * @see ParticipationRequestDto
 */
public class RequestDtoMapper {

    /**
     * Преобразует одиночную сущность запроса в DTO.
     * <p>
     * Создает объект {@link ParticipationRequestDto} на основе данных из сущности запроса,
     * извлекая необходимые идентификаторы из связанных сущностей события и заявителя.
     * </p>
     *
     * @param request сущность запроса на участие
     * @return DTO запроса, готовое для отправки клиенту
     */
    public static ParticipationRequestDto mapRequestToDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .created(request.getCreatedOn())
                .eventId(request.getEvent().getId())
                .requesterId(request.getRequester().getId())
                .build();
    }

    /**
     * Преобразует список сущностей запросов в список DTO.
     * <p>
     * Применяет {@link #mapRequestToDto(Request)} к каждому элементу списка
     * и собирает результаты в новый список.
     * </p>
     *
     * @param requests список сущностей запросов на участие
     * @return список DTO запросов, готовых для отправки клиенту
     */
    public static List<ParticipationRequestDto> mapRequestToDto(List<Request> requests) {
        return requests.stream().map(RequestDtoMapper::mapRequestToDto).toList();
    }
}