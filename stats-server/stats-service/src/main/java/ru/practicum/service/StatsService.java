package ru.practicum.service;

import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;

import java.util.List;

/**
 * Сервис для управления статистикой просмотров.
 * <p>
 * Определяет бизнес-логику для работы со статистикой: сохранение информации
 * о запросах к эндпоинтам (хитах) и получение агрегированных статистических данных
 * за указанный период с возможностью фильтрации и выбора режима уникальности.
 * </p>
 *
 * @see ru.practicum.controller.StatsController
 * @see HitCreateDto
 * @see RequestStatsDto
 * @see ResponseStatsDto
 */
public interface StatsService {

    /**
     * Сохраняет информацию о запросе к эндпоинту (хит).
     * <p>
     * Принимает данные о посещении и сохраняет их для последующего
     * анализа и формирования статистики.
     * </p>
     *
     * @param createDto DTO с данными о запросе (приложение, URI, IP, временная метка)
     */
    void saveHit(HitCreateDto createDto);

    /**
     * Возвращает статистику по просмотрам за указанный период.
     * <p>
     * Позволяет получить агрегированные данные о количестве просмотров
     * с фильтрацией по списку URI и выбором режима уникальности:
     * <ul>
     *   <li>unique = false - учитываются все просмотры, включая повторные с одного IP</li>
     *   <li>unique = true - учитываются только уникальные IP-адреса</li>
     * </ul>
     * </p>
     *
     * @param request DTO с параметрами запроса (start, end, uris, unique)
     * @return список DTO с данными статистики (app, uri, hits)
     */
    List<ResponseStatsDto> getStats(RequestStatsDto request);
}