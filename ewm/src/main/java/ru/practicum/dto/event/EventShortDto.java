package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

public record EventShortDto(
        String annotation,
        CategoryDto category,
        Long confirmedRequests,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime eventDate,
        Long id,
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views
) {
    @Builder(toBuilder = true)
    public EventShortDto {
    }
}
