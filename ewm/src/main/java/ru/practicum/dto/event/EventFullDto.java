package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.event.Location;
import ru.practicum.model.event.State;

import java.time.LocalDateTime;

public record EventFullDto(
        Long id,

        @NotBlank
        String annotation,

        @NotNull
        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdOn,

        String description,

        @NotNull
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,

        @NotNull
        UserShortDto initiator,

        @NotNull
        Location location,

        @NotNull
        Boolean paid,

        Integer participantLimit,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime publishedOn,

        Boolean requestModeration,

        State state,

        @NotBlank
        String title,

        Long views
) {
    @Builder(toBuilder = true)
    public EventFullDto {
    }
}
