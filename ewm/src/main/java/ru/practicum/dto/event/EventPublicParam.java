package ru.practicum.dto.event;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.model.event.EventSort;

import java.time.LocalDateTime;
import java.util.Set;

public record EventPublicParam(
        String text,
        Set<Long> categories,
        Boolean paid,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Future(message = "Поле rangeStart должно быть позже текущей даты и времени")
        LocalDateTime rangeStart,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime rangeEnd,

        Boolean onlyAvailable,
        EventSort sort,
        @Positive(message = "Параметр from должен быть больше нуля")
        Integer from,
        @Positive(message = "Параметр size должен быть больше нуля")
        Integer size
) {
    public EventPublicParam {
        from = from != null ? from : 0;
        size = size != null ? size : 10;
        onlyAvailable = onlyAvailable != null ? onlyAvailable : false;
    }

    @AssertTrue(message = "rangeEnd должен быть после rangeStart")
    public boolean isRangeValid() {
        return rangeStart == null || rangeEnd == null || rangeEnd.isAfter(rangeStart);
    }
}
