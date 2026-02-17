package ru.practicum.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) для запроса статистики из сервиса статистики.
 * <p>
 * Используется при запросе агрегированных данных о просмотрах за определенный период.
 * Содержит параметры фильтрации: диапазон дат, список URI и флаг уникальности просмотров.
 * </p>
 *
 * @param start начало временного периода для сбора статистики
 * @param end конец временного периода для сбора статистики
 * @param uris список URI для фильтрации (если null или пустой - статистика по всем URI)
 * @param unique флаг уникальности просмотров:
 *               true - учитывать только уникальные IP-адреса,
 *               false - учитывать все просмотры
 */
public record RequestStatsDto(
        LocalDateTime start,
        LocalDateTime end,
        List<String> uris,
        Boolean unique
) {
    /**
     * Конструктор с поддержкой паттерна Builder от Lombok.
     * Позволяет создавать объекты с использованием toBuilder().
     */
    @Builder(toBuilder = true)
    public RequestStatsDto {
    }
}