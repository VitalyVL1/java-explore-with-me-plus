package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.State;

import java.time.LocalDateTime;

public record EventFullDto(
        String annotation,
        CategoryDto category,
        Long confirmedRequests,
        LocalDateTime createdOn,
        String description,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Long id,
        UserShortDto initiator,
        Location location,
        Boolean paid,
        Integer participantLimit,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,
        Boolean requestModeration,
        State state,
        String title,
        Long views
) {
    @Builder(toBuilder = true)
    public EventFullDto {
    }
}
