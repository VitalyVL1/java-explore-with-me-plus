package ru.practicum.dto.compilation;

import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.compilation.Compilation;

import java.util.Set;

/**
 * Утилитарный класс-маппер для преобразования между сущностью подборки и DTO.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link Compilation}
 * в объект {@link CompilationDto}. Использует паттерн Builder для создания DTO.
 * </p>
 *
 * @see Compilation
 * @see CompilationDto
 * @see EventShortDto
 */
public class CompilationDtoMapper {

    /**
     * Преобразует сущность подборки в DTO с указанным списком событий.
     * <p>
     * Создает объект {@link CompilationDto} на основе данных из сущности подборки
     * и переданного множества DTO событий.
     * </p>
     *
     * @param compilation сущность подборки, содержащая основные данные
     * @param events множество DTO краткой информации о событиях, входящих в подборку
     * @return DTO подборки, готовое для отправки клиенту
     */
    public static CompilationDto mapCompilationToDto(Compilation compilation, Set<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .eventIds(events)
                .build();
    }
}