package ru.practicum.dto.event;

public record UpdateEventRequestParam(
        Long userId,
        Long eventId,
        UpdateEventRequest request

        ) {
}
