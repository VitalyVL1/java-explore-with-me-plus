package ru.practicum.model.mapper;

import ru.practicum.dto.HitCreateDto;
import ru.practicum.model.Stat;

/**
 * Утилитарный класс-маппер для преобразования между DTO и сущностью статистики.
 * <p>
 * Предоставляет статические методы для преобразования объекта {@link HitCreateDto}
 * в сущность {@link Stat} для сохранения в базу данных.
 * </p>
 *
 * @see Stat
 * @see HitCreateDto
 */
public class StatDtoMapper {

    /**
     * Преобразует DTO создания хита в сущность статистики.
     * <p>
     * Создает объект {@link Stat} на основе данных из DTO, используя паттерн Builder.
     * Все поля копируются напрямую без изменений.
     * </p>
     *
     * @param createDto DTO с данными о запросе (приложение, URI, IP, временная метка)
     * @return сущность статистики, готовая для сохранения в БД
     */
    public static Stat mapToModel(HitCreateDto createDto) {
        return Stat.builder()
                .app(createDto.app())
                .uri(createDto.uri())
                .ip(createDto.ip())
                .timestamp(createDto.timestamp())
                .build();
    }
}