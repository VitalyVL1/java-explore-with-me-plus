package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;
import ru.practicum.model.mapper.StatDtoMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для управления статистикой просмотров.
 * <p>
 * Обеспечивает бизнес-логику для сохранения информации о запросах к эндпоинтам
 * и получения агрегированных статистических данных с поддержкой фильтрации
 * по периоду, списку URI и режиму уникальности просмотров.
 * </p>
 *
 * @see StatsService
 * @see StatsRepository
 * @see StatDtoMapper
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    /**
     * Сохраняет информацию о запросе к эндпоинту (хит).
     * <p>
     * Преобразует DTO в сущность с помощью {@link StatDtoMapper}
     * и сохраняет в репозиторий.
     * </p>
     *
     * @param createDto DTO с данными о запросе (приложение, URI, IP, временная метка)
     */
    @Override
    public void saveHit(HitCreateDto createDto) {
        statsRepository.save(StatDtoMapper.mapToModel(createDto));
    }

    /**
     * Возвращает статистику по просмотрам за указанный период.
     * <p>
     * Выполняет следующие шаги:
     * <ul>
     *   <li>Обрабатывает список URI (преобразует пустой список в null для корректной работы JPQL)</li>
     *   <li>В зависимости от параметра unique вызывает соответствующий метод репозитория:
     *       {@link StatsRepository#findStats(LocalDateTime, LocalDateTime, List)} для всех просмотров,
     *       {@link StatsRepository#findUniqueStats(LocalDateTime, LocalDateTime, List)} для уникальных</li>
     * </ul>
     * </p>
     *
     * @param request DTO с параметрами запроса (start, end, uris, unique)
     * @return список DTO с данными статистики (app, uri, hits), отсортированный по убыванию hits
     */
    @Override
    public List<ResponseStatsDto> getStats(RequestStatsDto request) {
        List<String> requestUris;
        if (request.uris() == null || request.uris().isEmpty()) {
            requestUris = null;
        } else {
            requestUris = request.uris();
        }

        List<ResponseStatsDto> response;
        if (request.unique()) {
            response = statsRepository.findUniqueStats(request.start(), request.end(), requestUris);
        } else {
            response = statsRepository.findStats(request.start(), request.end(), requestUris);
        }

        return response;
    }
}