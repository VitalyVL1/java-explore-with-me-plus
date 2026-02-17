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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPrivateParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.dto.event.EventRequestStatusUpdateRequestParam;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventUserRequest;
import ru.practicum.dto.event.UpdateEventUserRequestParam;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.event.EventService;

import java.util.List;

/**
 * REST-контроллер для управления событиями авторизованными пользователями.
 * <p>
 * Предоставляет endpoints для создания, просмотра, обновления событий,
 * а также управления заявками на участие в событиях. Доступен только
 * аутентифицированным пользователям. Все операции выполняются от имени
 * конкретного пользователя, идентифицируемого по userId.
 * </p>
 *
 * @see EventService
 * @see EventFullDto
 * @see EventShortDto
 * @see NewEventDto
 * @see UpdateEventUserRequest
 * @see ParticipationRequestDto
 */
@RestController
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PrivateEventController {
    private final EventService eventService;
    private static final String USER_ID_VALIDATION_MESSAGE = "userId должен быть больше 0";
    private static final String EVENT_ID_VALIDATION_MESSAGE = "eventId должен быть больше 0";

    /**
     * Возвращает список событий, созданных указанным пользователем.
     * <p>
     * Позволяет пользователю просмотреть все свои события с поддержкой пагинации.
     * </p>
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param params параметры пагинации: from (индекс первого элемента) и size (количество на странице)
     * @return список DTO краткой информации о событиях пользователя
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> findUserEvents(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @Valid
            @ModelAttribute
            EventPrivateParam params
    ) {
        log.info("Private: Method launched (findUserEvents({}))", params);
        return eventService.findUserEvents(userId, params);
    }

    /**
     * Создает новое событие от имени пользователя.
     *
     * @param userId идентификатор автора события (должен быть положительным)
     * @param dto DTO с данными нового события
     * @return DTO созданного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @Valid
            @RequestBody
            NewEventDto dto
    ) {
        log.info("Private: Method launched (createEvent({}, {}))", userId, dto);
        return eventService.createEvent(userId, dto);
    }

    /**
     * Возвращает полную информацию о конкретном событии пользователя.
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param eventId идентификатор события (должен быть положительным)
     * @return DTO события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     * @throws jakarta.validation.ConstraintViolationException если userId или eventId не положительные
     */
    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto findUserEventById(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @PathVariable
            @Positive(message = EVENT_ID_VALIDATION_MESSAGE)
            Long eventId
    ) {
        log.info("Private: Method launched (findUserEventById({}, {}))", eventId, userId);
        return eventService.findUserEventById(eventId, userId);
    }

    /**
     * Обновляет событие, созданное пользователем.
     * <p>
     * Позволяет пользователю редактировать своё событие. В зависимости от действия
     * может отправлять событие на модерацию или отменять его.
     * </p>
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param eventId идентификатор события (должен быть положительным)
     * @param updateRequest DTO с обновленными данными и действием (SEND_TO_REVIEW, CANCEL_REVIEW)
     * @return DTO обновленного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие или пользователь не найдены
     * @throws ru.practicum.exception.ConditionsNotMetException если событие не может быть обновлено в текущем статусе
     * @throws jakarta.validation.ConstraintViolationException если userId или eventId не положительные
     */
    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateUserEvent(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @PathVariable
            @Positive(message = EVENT_ID_VALIDATION_MESSAGE)
            Long eventId,

            @Valid
            @RequestBody
            UpdateEventUserRequest updateRequest
    ) {
        UpdateEventUserRequestParam updateEventUserRequestParam =
                new UpdateEventUserRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateUserEvent({}))", updateEventUserRequestParam);
        return eventService.updateUserEvent(updateEventUserRequestParam);
    }


    /**
     * Возвращает список заявок на участие в событии пользователя.
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param eventId идентификатор события (должен быть положительным)
     * @return список DTO заявок на участие
     * @throws jakarta.validation.ConstraintViolationException если userId или eventId не положительные
     */
    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findEventRequests(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @PathVariable
            @Positive(message = EVENT_ID_VALIDATION_MESSAGE)
            Long eventId
    ) {
        log.info("Private: Method launched (findEventRequests({}, {}))", eventId, userId);
        return eventService.findEventRequests(eventId, userId);
    }

    /**
     * Обновляет статус заявок на участие в событии.
     * <p>
     * Позволяет пользователю подтверждать или отклонять заявки других пользователей
     * на участие в своём событии. Учитывает лимит участников события.
     * </p>
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param eventId идентификатор события (должен быть положительным)
     * @param updateRequest DTO со списком ID заявок и действием (CONFIRMED, REJECTED)
     * @return результат обновления с подтвержденными и отклоненными заявками
     * @throws ru.practicum.exception.NotFoundException если событие, пользователь или заявки не найдены
     * @throws ru.practicum.exception.ConditionsNotMetException если превышен лимит участников или заявка не в статусе PENDING
     * @throws jakarta.validation.ConstraintViolationException если userId или eventId не положительные
     */
    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable
            @Positive(message = USER_ID_VALIDATION_MESSAGE)
            Long userId,

            @PathVariable
            @Positive(message = EVENT_ID_VALIDATION_MESSAGE)
            Long eventId,

            @Valid
            @RequestBody
            EventRequestStatusUpdateRequest updateRequest
    ) {
        EventRequestStatusUpdateRequestParam updateEventRequestParam =
                new EventRequestStatusUpdateRequestParam(userId, eventId, updateRequest);
        log.info("Private: Method launched (updateRequestStatus({}))", updateEventRequestParam);
        return eventService.updateRequestStatus(updateEventRequestParam);
    }
}