package ru.practicum.dto.compilation;

import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.event.Event;

import java.util.Set;
import java.util.stream.Collectors;

public class CompilationDtoMapper {
    public static CompilationDto mapCompilationToDto(Compilation compilation, Set<Event> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .eventIds(events != null ? events.stream().map(Event::getId).collect(Collectors.toSet()) : Set.of())
                .build();
    }
}
