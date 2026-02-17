package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер для сервиса статистики.
 * <p>
 * Предоставляет endpoints для сохранения информации о запросах к эндпоинтам (хитах)
 * и получения агрегированной статистики по посещениям за указанный период.
 * </p>
 *
 * @see StatsService
 * @see HitCreateDto
 * @see RequestStatsDto
 * @see ResponseStatsDto
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    /**
     * Сохраняет информацию о запросе к эндпоинту (хит).
     * <p>
     * Принимает данные о посещении: приложение, URI, IP-адрес и временную метку.
     * Сохраняет эту информацию для последующего сбора статистики.
     * </p>
     *
     * @param createDto DTO с данными о запросе
     */
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(
            @RequestBody @Valid HitCreateDto createDto
    ) {
        log.info("Запрос на сохранение информации о запросе к эндпоинту с данными: {}", createDto);
        statsService.saveHit(createDto);
    }

    /**
     * Возвращает статистику по посещениям за указанный период.
     * <p>
     * Позволяет получить агрегированные данные о количестве просмотров
     * с фильтрацией по списку URI и выбором режима уникальности (по IP).
     * </p>
     *
     * @param start начало периода (обязательный параметр)
     * @param end конец периода (обязательный параметр, должен быть позже start)
     * @param uris список URI для фильтрации (опционально)
     * @param unique режим уникальности: true - только уникальные IP, false - все просмотры
     * @return список DTO с данными статистики (app, uri, hits)
     * @throws ValidationException если end не позже start
     */
    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseStatsDto> getStats(
            @RequestParam(name = "start")
            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
            LocalDateTime start,

            @RequestParam(name = "end")
            @DateTimeFormat(pattern = DATE_TIME_PATTERN)
            LocalDateTime end,

            @RequestParam(name = "uris", required = false)
            List<String> uris,

            @RequestParam(name = "unique", defaultValue = "false")
            Boolean unique
    ) {
        log.info("Запрос на получение статистики по посещениям с данными: start = {}, end = {}, uris = {}, unique = {}",
                start, end, uris, unique);
        if (start != null && end != null && !end.isAfter(start)) {
            throw new ValidationException("Дата конца диапазона должна быть позже даты начала");
        }
        return statsService.getStats(new RequestStatsDto(start, end, uris, unique));
    }
}