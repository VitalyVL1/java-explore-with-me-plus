package ru.practicum.service.request;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.dto.request.RequestDtoMapper;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.State;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация сервиса для управления заявками на участие в событиях.
 * <p>
 * Обеспечивает бизнес-логику для операций с заявками: создание, отмена,
 * получение списка заявок пользователя. Содержит валидацию правил создания заявок:
 * проверка статуса события, лимита участников, дублирования заявок и т.д.
 * </p>
 *
 * @see RequestService
 * @see RequestRepository
 * @see RequestDtoMapper
 */
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    /**
     * Возвращает список всех заявок на участие указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO заявок на участие
     * @throws NotFoundException если пользователь не найден
     */
    @Override
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        User user = getUserById(userId);

        return requestRepository.findUserRequests(user.getId()).stream()
                .map(RequestDtoMapper::mapRequestToDto)
                .toList();
    }

    /**
     * Создает новую заявку на участие в событии.
     * <p>
     * Выполняет комплексную валидацию:
     * <ul>
     *   <li>Пользователь не может подать заявку на свое событие</li>
     *   <li>Событие должно быть опубликовано</li>
     *   <li>Заявка не должна дублироваться</li>
     *   <li>Не должен быть превышен лимит участников</li>
     * </ul>
     * Статус заявки зависит от настроек модерации события.
     * </p>
     *
     * @param userId идентификатор пользователя, подающего заявку
     * @param eventId идентификатор события
     * @return DTO созданной заявки
     * @throws NotFoundException если пользователь или событие не найдены
     * @throws AlreadyExistsException если нарушены правила создания заявки
     */
    @Override
    public ParticipationRequestDto createRequest(long userId, long eventId) {
        User user = getUserById(userId);
        Event event = getEventById(eventId);

        validateCreation(user, event);

        return RequestDtoMapper.mapRequestToDto(requestRepository.save(Request.builder()
                .createdOn(LocalDateTime.now())
                .event(event)
                .requester(user)
                .status(event.getRequestModeration() && event.getParticipantLimit() > 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .build()));
    }

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
     * @throws NotFoundException если пользователь или заявка не найдены
     * @throws ValidationException если пользователь не является владельцем заявки
     *         или статус заявки не позволяет отмену
     */
    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        User user = getUserById(userId);
        Request request = getRequestById(requestId);

        if (request.getRequester().getId() != user.getId()) {
            throw new ValidationException("Пользователь id=" + user.getId() + " не может отменить заявку id=" + request.getId());
        }

        if (RequestStatus.CANCELED.equals(request.getStatus()) || RequestStatus.REJECTED.equals(request.getStatus())) {
            throw new ValidationException("Статус заявки " + request.getStatus() + " не позволяет выполнить отмену");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestDtoMapper.mapRequestToDto(requestRepository.save(request));
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return сущность User
     * @throws NotFoundException если пользователь не найден
     */
    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    /**
     * Получает заявку по идентификатору.
     *
     * @param requestId идентификатор заявки
     * @return сущность Request
     * @throws NotFoundException если заявка не найдена
     */
    private Request getRequestById(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Заявка с id " + requestId + " не найдена"));
    }

    /**
     * Получает событие по идентификатору.
     *
     * @param eventId идентификатор события
     * @return сущность Event
     * @throws NotFoundException если событие не найдено
     */
    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
    }

    /**
     * Выполняет валидацию перед созданием заявки.
     *
     * @param user пользователь, подающий заявку
     * @param event событие, на которое подается заявка
     * @throws AlreadyExistsException если нарушено одно из правил создания заявки
     */
    private void validateCreation(User user, Event event) {
        if (user.getId() == event.getInitiator().getId()) {
            throw new AlreadyExistsException("Пользователь не может подать запрос на участие в своем событии");
        }

        if (!State.PUBLISHED.equals(event.getState())) {
            throw new AlreadyExistsException("Нельзя участвовать в неопубликованном событии");
        }

        if (requestRepository.findByUserAndEvent(user.getId(), event.getId()).isPresent()) {
            throw new AlreadyExistsException("Запрос на участие в этом событии уже существует");
        }

        if (event.getParticipantLimit() != 0 &&
            requestRepository.countByEventAndStatus(event.getId(), RequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new AlreadyExistsException("Достигнут лимит запросов на участие в событии");
        }
    }
}