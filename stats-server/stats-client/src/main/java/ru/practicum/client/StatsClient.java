package ru.practicum.client;

import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;

import java.util.List;

/**
 * Интерфейс клиента для взаимодействия с сервисом статистики.
 * <p>
 * Определяет контракт для отправки информации о просмотрах (хитах) и получения
 * статистических данных из сервиса статистики. Реализации этого интерфейса
 * могут использовать различные способы коммуникации (REST, gRPC и т.д.).
 * </p>
 *
 * @see ru.practicum.client.StatsClientRestImpl
 * @see HitCreateDto
 * @see RequestStatsDto
 * @see ResponseStatsDto
 */
public interface StatsClient {

    /**
     * Отправляет информацию о просмотре (хите) в сервис статистики.
     * <p>
     * Используется для сохранения данных о каждом обращении к конкретному эндпоинту.
     * </p>
     *
     * @param dto объект с данными о просмотре (приложение, URI, IP адрес, временная метка)
     */
    void hit(HitCreateDto dto);

    /**
     * Получает статистику по запросам из сервиса статистики за указанный период.
     * <p>
     * Позволяет получить агрегированные данные о количестве просмотров
     * с возможностью фильтрации по URI и выбором уникальных/всех просмотров.
     * </p>
     *
     * @param request объект с параметрами запроса статистики:
     *               - start: начало периода
     *               - end: конец периода
     *               - uris: список URI для фильтрации (опционально)
     *               - unique: true - только уникальные просмотры (по IP), false - все просмотры
     * @return список DTO с данными статистики (URI, количество просмотров)
     */
    List<ResponseStatsDto> get(RequestStatsDto request);
}