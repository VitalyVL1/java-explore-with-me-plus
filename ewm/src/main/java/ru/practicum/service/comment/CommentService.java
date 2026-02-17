package ru.practicum.service.comment;

import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.model.comment.CommentState;
import ru.practicum.model.comment.DateSort;

import java.util.List;

/**
 * Сервис для управления комментариями.
 * <p>
 * Определяет бизнес-логику для работы с комментариями на различных уровнях доступа:
 * <ul>
 *   <li>Пользовательский - создание, просмотр, обновление, удаление своих комментариев</li>
 *   <li>Административный - модерация, поиск, удаление любых комментариев</li>
 *   <li>Публичный - просмотр подтвержденных комментариев к событиям</li>
 * </ul>
 * </p>
 *
 * @see ru.practicum.controller.comment.PrivateCommentController
 * @see ru.practicum.controller.comment.AdminCommentController
 * @see ru.practicum.controller.event.PublicEventController
 * @see CommentDto
 * @see StateCommentDto
 * @see CommentState
 */
public interface CommentService {

    /**
     * Создает новый комментарий от имени пользователя.
     *
     * @param userId идентификатор автора комментария
     * @param commentDto DTO с данными нового комментария
     * @return DTO созданного комментария
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие не найдены
     */
    CommentDto createComment(long userId, NewCommentDto commentDto);

    /**
     * Возвращает все комментарии указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO комментариев пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь не найден
     */
    List<CommentDto> getComments(long userId);

    /**
     * Обновляет существующий комментарий.
     *
     * @param userId идентификатор пользователя
     * @param commentDto DTO с обновленными данными комментария
     * @return DTO обновленного комментария
     * @throws ru.practicum.exception.NotFoundException если пользователь или комментарий не найдены
     * @throws ru.practicum.exception.AccessDeniedException если пользователь не является автором
     */
    CommentDto updateComment(long userId, UpdateCommentDto commentDto);

    /**
     * Удаляет комментарий пользователя.
     *
     * @param userId идентификатор пользователя
     * @param comId идентификатор комментария
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     * @throws ru.practicum.exception.AccessDeniedException если пользователь не является автором
     */
    void deleteComment(long userId, long comId);

    /**
     * Возвращает все комментарии с фильтрацией по тексту и сортировкой (для администратора).
     *
     * @param text текст для поиска (опционально)
     * @param sort направление сортировки по дате
     * @return список DTO комментариев для администратора
     */
    List<StateCommentDto> getComments(String text, DateSort sort);

    /**
     * Проводит модерацию комментария.
     *
     * @param comId идентификатор комментария
     * @param approved true - подтвердить комментарий, false - отклонить
     * @return DTO комментария с обновленным статусом
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     * @throws ru.practicum.exception.CommentStateException если комментарий не в статусе WAITING
     */
    StateCommentDto reviewComment(long comId, boolean approved);

    /**
     * Удаляет комментарий (административный доступ).
     *
     * @param comId идентификатор комментария
     * @throws ru.practicum.exception.NotFoundException если комментарий не найден
     */
    void deleteComment(long comId);

    /**
     * Возвращает комментарии с указанным статусом.
     *
     * @param state статус комментария
     * @param sort направление сортировки по дате
     * @return список DTO комментариев
     */
    List<CommentDto> getCommentsByState(CommentState state, DateSort sort);

    /**
     * Возвращает комментарии к указанному событию.
     *
     * @param eventId идентификатор события
     * @param sort направление сортировки по дате
     * @return список DTO комментариев
     */
    List<CommentDto> getCommentsByEvent(long eventId, DateSort sort);
}