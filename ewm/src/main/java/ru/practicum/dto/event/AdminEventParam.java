package ru.practicum.dto.event;

import ru.practicum.model.event.State;

import java.time.LocalDateTime;
import java.util.List;

public record AdminEventParam(
        List<Long> users,
        List<State> states,
        List<Long> categories,
        LocalDateTime rangeStart,
        LocalDateTime rangeEnd,
        int from,
        int size
) {
}
