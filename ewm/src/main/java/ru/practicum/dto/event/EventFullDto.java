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

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для полного представления события.
 * <p>
 * Используется для передачи детальной информации о событии клиенту.
 * Содержит все поля события, включая информацию о категории, инициаторе,
 * местоположении, статусе, количестве подтвержденных запросов и просмотров.
 * </p>
 *
 * @param id уникальный идентификатор события
 * @param annotation краткое описание события
 * @param category категория события
 * @param confirmedRequests количество подтвержденных запросов на участие
 * @param createdOn дата и время создания события
 * @param description полное описание события
 * @param eventDate дата и время проведения события
 * @param initiator инициатор события
 * @param location местоположение события
 * @param paid флаг платности события
 * @param participantLimit лимит участников (0 - без лимита)
 * @param publishedOn дата и время публикации события
 * @param requestModeration флаг необходимости модерации заявок
 * @param state статус события
 * @param title заголовок события
 * @param views количество просмотров события
 */
public record EventFullDto(
        Long id,

        @NotBlank
        String annotation,

        @NotNull
        CategoryDto category,

        Long confirmedRequests,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime createdOn,

        String description,

        @NotNull
        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,

        @NotNull
        UserShortDto initiator,

        @NotNull
        Location location,

        @NotNull
        Boolean paid,

        Integer participantLimit,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime publishedOn,

        Boolean requestModeration,

        State state,

        @NotBlank
        String title,

        Long views
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать объекты с использованием toBuilder().
     */
    @Builder(toBuilder = true)
    public EventFullDto {
    }
}