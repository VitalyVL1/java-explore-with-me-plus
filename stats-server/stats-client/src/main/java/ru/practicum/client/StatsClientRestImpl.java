package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * REST-клиент для взаимодействия с сервисом статистики.
 * <p>
 * Реализует интерфейс {@link StatsClient} и предоставляет методы для отправки
 * информации о просмотрах (hit) и получения статистики по запросам.
 * Использует {@link RestClient} для выполнения HTTP-запросов к сервису статистики.
 * </p>
 *
 * @see StatsClient
 * @see RestClient
 * @see HitCreateDto
 * @see RequestStatsDto
 * @see ResponseStatsDto
 */
@Slf4j
@Component
public class StatsClientRestImpl implements StatsClient {

    private final String baseUrl;
    private final DateTimeFormatter dateTimeFormatter;
    private final RestClient restClient;

    /**
     * Конструктор клиента статистики.
     *
     * @param baseUrl базовый URL сервиса статистики (из конфигурации stats.service.url)
     * @param dateTamePattern шаблон форматирования даты и времени для запросов
     *                        (из конфигурации stats.date_time.format)
     */
    public StatsClientRestImpl(
            @Value("${stats.service.url}") String baseUrl,
            @Value("${stats.date_time.format}") String dateTamePattern
    ) {
        this.baseUrl = baseUrl;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTamePattern);
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Отправляет информацию о просмотре (хите) в сервис статистики.
     * <p>
     * Выполняет POST-запрос к эндпоинту /hit сервиса статистики.
     * В случае ошибки логирует предупреждение и пробрасывает исключение дальше.
     * </p>
     *
     * @param dto объект с данными о просмотре (приложение, URI, IP, временная метка)
     * @throws RuntimeException если запрос к сервису статистики не удался
     */
    @Override
    public void hit(HitCreateDto dto) {
        try {
            restClient.post()
                    .uri("/hit")
                    .contentType(APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Не удалось отправить хит в сервис статистики", e);
            throw e;
        }
    }

    /**
     * Получает статистику по запросам из сервиса статистики.
     * <p>
     * Выполняет GET-запрос к эндпоинту /stats с параметрами:
     * <ul>
     *   <li>start - начало периода (форматируется согласно dateTimeFormatter)</li>
     *   <li>end - конец периода</li>
     *   <li>unique - уникальные или все просмотры</li>
     *   <li>uris - список URI для фильтрации (опционально)</li>
     * </ul>
     * В случае ошибки логирует предупреждение и возвращает пустой список.
     * </p>
     *
     * @param requestStatsDto объект с параметрами запроса статистики
     * @return список DTO с данными статистики или пустой список в случае ошибки
     */
    @Override
    public List<ResponseStatsDto> get(RequestStatsDto requestStatsDto) {
        try {
            URI uri = UriComponentsBuilder.fromUri(URI.create(baseUrl))
                    .path("/stats")
                    .queryParam("start", requestStatsDto.start().format(dateTimeFormatter))
                    .queryParam("end", requestStatsDto.end().format(dateTimeFormatter))
                    .queryParam("unique", requestStatsDto.unique())
                    .queryParamIfPresent("uris", Optional.ofNullable(requestStatsDto.uris()))
                    .build()
                    .toUri();

            return restClient.get()
                    .uri(uri)
                    .accept(APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (Exception e) {
            log.warn("Не удалось получить статистику из сервиса статистики", e);
            return Collections.emptyList();
        }
    }
}