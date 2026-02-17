package ru.practicum.dto.compilation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.dto.event.EventShortDto;

import java.util.Set;

/**
 * DTO (Data Transfer Object) для представления подборки событий.
 * <p>
 * Используется для передачи информации о подборке событий клиенту.
 * Содержит основную информацию о подборке и список событий, входящих в неё.
 * </p>
 *
 * @see EventShortDto
 */
@Data
@Builder
public class CompilationDto {
    /**
     * Уникальный идентификатор подборки.
     */
    private Long id;

    /**
     * Заголовок подборки.
     */
    private String title;

    /**
     * Флаг закрепления подборки на главной странице.
     * true - подборка закреплена и отображается в верхней части,
     * false - подборка не закреплена.
     */
    private boolean pinned;

    /**
     * Множество событий, входящих в данную подборку.
     * Аннотация {@link JsonProperty} используется для корректной сериализации
     * в JSON с именем поля "events".
     */
    @JsonProperty("events")
    private Set<EventShortDto> eventIds;
}