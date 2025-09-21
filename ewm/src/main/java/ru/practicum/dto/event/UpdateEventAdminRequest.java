package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.model.event.Location;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(
        @Size(min = 20, max = 2000, message = "Длина аннотации должна быть от 20 до 2000 символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Длина описания должна быть от 20 до 7000 символов")
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Future(message = "Дата начала события должна быть в будущем")
        LocalDateTime eventDate,

        Location location,

        Boolean paid,

        @PositiveOrZero(message = "Лимит пользователей должен быть положительным числом")
        Integer participantLimit,

        Boolean requestModeration,

        StateAction stateAction,

        @Size(min = 3, max = 120, message = "Длина названия должна быть от3 до 120 символов")
        String title
) {
}
