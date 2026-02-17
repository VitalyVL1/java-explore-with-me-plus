package ru.practicum.controller.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.service.event.EventService;

import java.util.List;

/**
 * REST-контроллер для административного управления событиями.
 * <p>
 * Предоставляет endpoints для поиска событий с расширенной фильтрацией
 * и модерации событий (публикация/отклонение). Доступен только пользователям
 * с ролью администратора.
 * </p>
 *
 * @see EventService
 * @see EventFullDto
 * @see AdminEventParam
 * @see UpdateEventAdminRequest
 */
@RestController
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    /**
     * Возвращает список событий с расширенной фильтрацией для администратора.
     * <p>
     * Позволяет администратору искать события по различным критериям:
     * списку пользователей, списку состояний, списку категорий, диапазону дат,
     * с поддержкой пагинации.
     * </p>
     *
     * @param params объект с параметрами фильтрации и пагинации:
     *               - users: список ID пользователей
     *               - states: список состояний событий
     *               - categories: список ID категорий
     *               - rangeStart: начало диапазона дат
     *               - rangeEnd: конец диапазона дат
     *               - from: количество элементов для пропуска
     *               - size: количество элементов на странице
     * @return список DTO событий с полной информацией для администратора
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> findAll(
            @Valid @ModelAttribute AdminEventParam params
    ) {
        log.info("Admin: Method launched (findAllAdmin({}))", params);
        return eventService.findAllAdmin(params);
    }

    /**
     * Обновляет событие (модерирует его) от имени администратора.
     * <p>
     * Позволяет администратору публиковать или отклонять события, а также
     * редактировать их данные в процессе модерации.
     * </p>
     *
     * @param eventId идентификатор обновляемого события (должен быть положительным)
     * @param event объект с данными для обновления события:
     *             - stateAction: действие над событием (PUBLISH_EVENT, REJECT_EVENT)
     *             - другие поля для редактирования
     * @return DTO обновленного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие с указанным ID не найдено
     * @throws ru.practicum.exception.ConditionsNotMetException если событие не соответствует правилам публикации
     * @throws jakarta.validation.ConstraintViolationException если eventId не положительный
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(
            @PathVariable @Positive Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest event
    ) {
        log.info("Admin: Method launched (update(Long eventId = {}, UpdateEventAdminRequest event = {}))", eventId, event);
        return eventService.updateAdminEvent(eventId, event);
    }
}