package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.util.DateTimeFormat.DATE_TIME_PATTERN;

/**
 * DTO (Data Transfer Object) для краткого представления события.
 * <p>
 * Используется для передачи основной информации о событии в списках и кратких обзорах.
 * Содержит ключевые поля события без детального описания и внутренних статусов.
 * </p>
 *
 * @param annotation краткое описание события
 * @param category категория события
 * @param confirmedRequests количество подтвержденных запросов на участие
 * @param eventDate дата и время проведения события
 * @param id уникальный идентификатор события
 * @param initiator инициатор события
 * @param paid флаг платности события
 * @param title заголовок события
 * @param views количество просмотров события
 */
public record EventShortDto(
        String annotation,
        CategoryDto category,
        Long confirmedRequests,

        @JsonFormat(pattern = DATE_TIME_PATTERN)
        LocalDateTime eventDate,
        Long id,
        UserShortDto initiator,
        Boolean paid,
        String title,
        Long views
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать объекты с использованием toBuilder().
     */
    @Builder(toBuilder = true)
    public EventShortDto {
    }
}