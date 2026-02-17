package ru.practicum.controller.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.comment.CommentState;
import ru.practicum.model.comment.DateSort;
import ru.practicum.service.comment.CommentService;
import ru.practicum.service.event.EventService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер для публичного доступа к событиям и комментариям.
 * <p>
 * Предоставляет endpoints для поиска событий с расширенной фильтрацией,
 * просмотра детальной информации о событиях, а также получения комментариев.
 * Доступен всем пользователям без необходимости аутентификации.
 * Автоматически сохраняет статистику просмотров событий.
 * </p>
 *
 * @see EventService
 * @see CommentService
 * @see StatsClient
 * @see EventFullDto
 * @see EventShortDto
 * @see CommentDto
 */
@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PublicEventController {
    private final EventService eventService;
    private final StatsClient statsClient;
    private final CommentService commentService;

    @Value("${stats.service.name}")
    private String serviceName;

    /**
     * Возвращает список событий с возможностью расширенной фильтрации и пагинации.
     * <p>
     * Позволяет искать события по тексту, категориям, платности, диапазону дат,
     * а также сортировать по дате или количеству просмотров. При успешном поиске
     * сохраняет статистику запроса.
     * </p>
     *
     * @param params объект с параметрами фильтрации, пагинации и сортировки
     * @param request HTTP запрос для получения информации о клиенте и URI
     * @return список DTO событий с краткой информацией
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findPublicEvents(
            @Valid @ModelAttribute EventPublicParam params,
            HttpServletRequest request) {
        log.info("Public: Method launched (findPublicEvents({}))", params);
        List<EventShortDto> events = eventService.findPublicEvents(params);
        if (!events.isEmpty()) {
            saveHit(request);
        }
        return events;
    }

    /**
     * Возвращает детальную информацию о конкретном событии по его идентификатору.
     * <p>
     * При просмотре события автоматически увеличивает счетчик просмотров
     * и сохраняет статистику.
     * </p>
     *
     * @param id идентификатор события (должен быть положительным)
     * @param request HTTP запрос для получения информации о клиенте и URI
     * @return DTO события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие с указанным ID не найдено
     * @throws jakarta.validation.ConstraintViolationException если id не положительный
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findPublicEventById(
            @Positive(message = "eventId должен быть больше 0")
            @PathVariable
            Long id,
            HttpServletRequest request
    ) {
        log.info("Public: Method launched (findPublicEventById({}))", id);
        EventFullDto event = eventService.findPublicEventById(id);
        saveHit(request);
        return event;
    }

    /**
     * Возвращает все подтвержденные комментарии с возможностью сортировки по дате.
     *
     * @param sort направление сортировки по дате: ASC - по возрастанию, DESC - по убыванию
     * @return список DTO подтвержденных комментариев
     */
    @GetMapping("/comments")
    public List<CommentDto> getAllComments(@RequestParam(name = "sort", defaultValue = "ASC") DateSort sort) {
        log.info("Public: Get All comments, sort={}", sort);
        return commentService.getCommentsByState(CommentState.APPROVED, sort);
    }

    /**
     * Возвращает все подтвержденные комментарии к конкретному событию.
     *
     * @param eventId идентификатор события
     * @param sort направление сортировки по дате: ASC - по возрастанию, DESC - по убыванию
     * @return список DTO подтвержденных комментариев к указанному событию
     */
    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable(name = "eventId") long eventId,
                                             @RequestParam(name = "sort", defaultValue = "ASC") DateSort sort) {
        log.info("Public: Get event ({}) comments sort={}", eventId, sort);
        return commentService.getCommentsByEvent(eventId, sort);
    }

    /**
     * Сохраняет информацию о просмотре в сервис статистики.
     * <p>
     * Отправляет данные о запросе в Stats-сервис для последующего сбора статистики.
     * В случае ошибки сохранения статистики, только логирует предупреждение,
     * не прерывая основной поток выполнения.
     * </p>
     *
     * @param request HTTP запрос, из которого извлекаются URI и IP адрес клиента
     */
    private void saveHit(HttpServletRequest request) {
        try {
            statsClient.hit(HitCreateDto.builder()
                    .app(serviceName)
                    .uri(request.getRequestURI())
                    .ip(request.getRemoteAddr())
                    .timestamp(LocalDateTime.now())
                    .build());
        } catch (Exception e) {
            log.warn("Failed to save statistics for URI: {}", request.getRequestURI(), e);
            // Не бросаем исключение дальше - статистика не должна ломать основной flow
        }
    }
}