package ru.practicum.controller.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.request.RequestService;

import java.util.List;

/**
 * REST-контроллер для управления заявками на участие в событиях авторизованными пользователями.
 * <p>
 * Предоставляет endpoints для создания, просмотра и отмены заявок на участие в событиях.
 * Доступен только аутентифицированным пользователям. Все операции выполняются от имени
 * конкретного пользователя, идентифицируемого по userId.
 * </p>
 *
 * @see RequestService
 * @see ParticipationRequestDto
 */
@RestController
@RequestMapping("/users/{userId}/requests")
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestService requestService;

    /**
     * Возвращает список всех заявок на участие, созданных указанным пользователем.
     * <p>
     * Позволяет пользователю просмотреть все свои заявки на участие в событиях других пользователей.
     * </p>
     *
     * @param userId идентификатор пользователя
     * @return список DTO заявок на участие пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     */
    @GetMapping
    public List<ParticipationRequestDto> getUserRequests(@PathVariable(name = "userId") long userId) {
        log.info("Private: getting requests for userId={}", userId);
        return requestService.getUserRequests(userId);
    }

    /**
     * Создает новую заявку на участие в событии.
     * <p>
     * Позволяет пользователю подать заявку на участие в событии другого пользователя.
     * Заявка создается со статусом PENDING и ожидает подтверждения инициатором события.
     * </p>
     *
     * @param userId идентификатор пользователя, подающего заявку
     * @param eventId идентификатор события, на которое подается заявка
     * @return DTO созданной заявки на участие
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие с указанными ID не найдены
     * @throws ru.practicum.exception.AlreadyExistsException если:
     *         - пользователь является инициатором события
     *         - событие не опубликовано
     *         - превышен лимит участников
     *         - заявка уже существует
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(name = "userId") long userId,
                                                 @RequestParam(name = "eventId") long eventId) {
        log.info("Private: creating request for userId={}, eventId={}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    /**
     * Отменяет заявку на участие.
     * <p>
     * Позволяет пользователю отменить свою ранее созданную заявку на участие в событии.
     * </p>
     *
     * @param userId идентификатор пользователя, отменяющего заявку
     * @param requestId идентификатор отменяемой заявки
     * @return DTO отмененной заявки с обновленным статусом
     * @throws ru.practicum.exception.NotFoundException если заявка с указанным ID не найдена
     * @throws ru.practicum.exception.ValidationException если пользователь не является владельцем заявки
     */
    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable(name = "userId") long userId,
                                                 @PathVariable(name = "requestId") long requestId) {
        log.info("Private: cancelling request for userId={}, requestId={}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}