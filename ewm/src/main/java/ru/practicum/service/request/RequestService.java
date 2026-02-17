package ru.practicum.service.request;

import ru.practicum.dto.request.ParticipationRequestDto;

import java.util.List;

/**
 * Сервис для управления заявками на участие в событиях.
 * <p>
 * Определяет бизнес-логику для работы с заявками от имени авторизованных пользователей:
 * создание новых заявок, отмена существующих, получение списка своих заявок.
 * </p>
 *
 * @see ru.practicum.controller.request.PrivateRequestController
 * @see ParticipationRequestDto
 */
public interface RequestService {

    /**
     * Возвращает список всех заявок на участие указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO заявок на участие
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    List<ParticipationRequestDto> getUserRequests(long userId);

    /**
     * Создает новую заявку на участие в событии.
     * <p>
     * При создании проверяются следующие условия:
     * <ul>
     *   <li>пользователь не является инициатором события</li>
     *   <li>событие опубликовано</li>
     *   <li>заявка не дублируется</li>
     *   <li>не превышен лимит участников</li>
     * </ul>
     * </p>
     *
     * @param userId идентификатор пользователя, подающего заявку
     * @param eventId идентификатор события
     * @return DTO созданной заявки
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие не найдены
     * @throws ru.practicum.exception.AlreadyExistsException если нарушены правила создания заявки
     */
    ParticipationRequestDto createRequest(long userId, long eventId);

    /**
     * Отменяет заявку на участие.
     * <p>
     * Проверяет, что пользователь является владельцем заявки и что статус заявки
     * позволяет выполнить отмену (не CANCELED и не REJECTED).
     * </p>
     *
     * @param userId идентификатор пользователя
     * @param requestId идентификатор заявки
     * @return DTO отмененной заявки
     * @throws ru.practicum.exception.NotFoundException если пользователь или заявка не найдены
     * @throws ru.practicum.exception.ValidationException если пользователь не является владельцем заявки
     *         или статус заявки не позволяет отмену
     */
    ParticipationRequestDto cancelRequest(long userId, long requestId);
}