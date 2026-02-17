package ru.practicum.service.event;

import ru.practicum.dto.event.AdminEventParam;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventPrivateParam;
import ru.practicum.dto.event.EventPublicParam;
import ru.practicum.dto.event.EventRequestStatusUpdateRequestParam;
import ru.practicum.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.dto.event.UpdateEventUserRequestParam;
import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

/**
 * Сервис для управления событиями.
 * <p>
 * Определяет бизнес-логику для работы с событиями на различных уровнях доступа:
 * <ul>
 *   <li>Публичный - поиск событий с фильтрацией, просмотр детальной информации</li>
 *   <li>Пользовательский - создание, обновление, управление заявками на участие</li>
 *   <li>Административный - модерация, расширенный поиск с фильтрацией</li>
 * </ul>
 * Взаимодействует с сервисом статистики для учета просмотров событий.
 * </p>
 *
 * @see ru.practicum.controller.event.PublicEventController
 * @see ru.practicum.controller.event.PrivateEventController
 * @see ru.practicum.controller.event.AdminEventController
 * @see EventFullDto
 * @see EventShortDto
 * @see ParticipationRequestDto
 */
public interface EventService {

    /**
     * Возвращает список событий с расширенной фильтрацией для администратора.
     *
     * @param params параметры фильтрации и пагинации (списки пользователей, статусов, категорий, диапазон дат)
     * @return список DTO событий с полной информацией
     */
    List<EventFullDto> findAllAdmin(AdminEventParam params);

    /**
     * Обновляет событие администратором (модерация).
     *
     * @param id идентификатор события
     * @param event DTO с данными для обновления и действием (PUBLISH_EVENT, REJECT_EVENT)
     * @return DTO обновленного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие не найдено
     * @throws ru.practicum.exception.ConditionsNotMetException если событие не может быть обновлено в текущем статусе
     */
    EventFullDto updateAdminEvent(long id, UpdateEventAdminRequest event);

    /**
     * Возвращает список событий для публичного доступа с фильтрацией и сортировкой.
     *
     * @param params параметры фильтрации, сортировки и пагинации
     * @return список DTO событий с краткой информацией
     */
    List<EventShortDto> findPublicEvents(EventPublicParam params);

    /**
     * Возвращает детальную информацию о событии по его идентификатору для публичного доступа.
     *
     * @param eventId идентификатор события
     * @return DTO события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не опубликовано
     */
    EventFullDto findPublicEventById(Long eventId);

    /**
     * Возвращает список событий, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @param params параметры пагинации
     * @return список DTO событий с краткой информацией
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    List<EventShortDto> findUserEvents(Long userId, EventPrivateParam params);

    /**
     * Создает новое событие от имени пользователя.
     *
     * @param userId идентификатор пользователя
     * @param dto DTO с данными нового события
     * @return DTO созданного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если пользователь или категория не найдены
     */
    EventFullDto createEvent(Long userId, NewEventDto dto);

    /**
     * Возвращает детальную информацию о конкретном событии пользователя.
     *
     * @param eventId идентификатор события
     * @param userId идентификатор пользователя
     * @return DTO события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     */
    EventFullDto findUserEventById(Long eventId, Long userId);

    /**
     * Обновляет событие пользователя.
     *
     * @param requestParam составной параметр с идентификаторами и DTO обновления
     * @return DTO обновленного события с полной информацией
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     * @throws ru.practicum.exception.ConditionsNotMetException если событие не может быть обновлено
     */
    EventFullDto updateUserEvent(UpdateEventUserRequestParam requestParam);

    /**
     * Возвращает список заявок на участие в событии пользователя.
     *
     * @param eventId идентификатор события
     * @param userId идентификатор пользователя
     * @return список DTO заявок на участие
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     */
    List<ParticipationRequestDto> findEventRequests(Long eventId, Long userId);

    /**
     * Обновляет статусы заявок на участие в событии.
     *
     * @param requestParam составной параметр с идентификаторами и запросом на обновление
     * @return результат обновления с подтвержденными и отклоненными заявками
     * @throws ru.practicum.exception.NotFoundException если событие не найдено или не принадлежит пользователю
     * @throws ru.practicum.exception.ConditionsNotMetException если достигнут лимит участников или заявки в некорректном статусе
     */
    EventRequestStatusUpdateResult updateRequestStatus(EventRequestStatusUpdateRequestParam requestParam);
}