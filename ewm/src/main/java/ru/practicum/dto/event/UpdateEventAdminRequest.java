package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.StateAction;

import java.time.LocalDateTime;

public record UpdateEventAdminRequest(
        @Size(min = 20, max = 2000, message = "Аннотация должна быть не менее 20 и не более 2000 символов")
        String annotation,

        Long category,

        @Size(min = 20, max = 7000, message = "Описание должно быть не менее 20 и не более 7000 символов")
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Future(message = "Дата события должна быть в будущем")
        LocalDateTime eventDate,

        Location location,
        Boolean paid,

        @PositiveOrZero(message = "Лимит участников должен быть неотрицательным")
        Integer participantLimit,
        Boolean requestModeration,
        StateAction stateAction,

        @Size(min = 3, max = 120, message = "Название должно быть не менее 3 и не более 200 символов")
        String title
) {
    @AssertTrue(message = "Администратор может использовать только PUBLISH_EVENT или REJECT_EVENT")
    public boolean isValidStateAction() {
        return stateAction == null ||
               stateAction == StateAction.PUBLISH_EVENT ||
               stateAction == StateAction.REJECT_EVENT;
    }
}
