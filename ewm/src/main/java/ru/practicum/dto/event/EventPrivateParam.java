package ru.practicum.dto.event;

import jakarta.validation.constraints.Positive;

public record EventPrivateParam(
        @Positive(message = "Параметр from должен быть больше нуля")
        Integer from,
        @Positive(message = "Параметр size должен быть больше нуля")
        Integer size
) {
    public EventPrivateParam {
        from = from != null ? from : 0;
        size = size != null ? size : 10;
    }
}
