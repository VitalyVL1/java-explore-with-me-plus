package ru.practicum.model.compilation;

import ru.practicum.model.event.Event;

/**
 * Запись (Record) для представления связи между подборкой и событием в виде плоской структуры.
 * <p>
 * Используется для эффективного получения данных о связях подборок и событий
 * без необходимости загружать полные сущности {@link CompilationEvent}.
 * Применяется в запросах с группировкой для построения маппинга.
 * </p>
 *
 * @param compilationId идентификатор подборки
 * @param event сущность события, связанного с подборкой
 * @see CompilationEvent
 * @see ru.practicum.service.compilation.CompilationServiceImpl
 */
public record EventCompilationId(Long compilationId, Event event) {
}