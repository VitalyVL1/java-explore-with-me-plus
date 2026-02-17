package ru.practicum.controller.comment;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.UpdateCommentDto;
import ru.practicum.service.comment.CommentService;

import java.util.List;

/**
 * REST-контроллер для управления комментариями авторизованными пользователями.
 * <p>
 * Предоставляет endpoints для создания, просмотра, обновления и удаления комментариев.
 * Доступен только аутентифицированным пользователям. Все операции выполняются от имени
 * конкретного пользователя, идентифицируемого по userId.
 * </p>
 *
 * @see CommentService
 * @see CommentDto
 * @see NewCommentDto
 * @see UpdateCommentDto
 */
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateCommentController {
    private final CommentService commentService;

    /**
     * Возвращает список всех комментариев, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @return список DTO комментариев пользователя
     * @throws ru.practicum.exception.NotFoundException если пользователь с указанным ID не найден
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @GetMapping
    public List<CommentDto> getComments(@PathVariable @Positive long userId) {
        log.info("Private: Method launched (getComments({}))", userId);
        return commentService.getComments(userId);
    }

    /**
     * Создает новый комментарий от имени пользователя.
     * <p>
     * Новый комментарий создается со статусом WAITING и требует модерации перед публикацией.
     * </p>
     *
     * @param userId идентификатор автора комментария (должен быть положительным)
     * @param commentDto DTO с данными нового комментария (содержит eventId и text)
     * @return DTO созданного комментария
     * @throws ru.practicum.exception.NotFoundException если пользователь или событие с указанными ID не найдены
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable @Positive long userId,
                                    @RequestBody @Valid NewCommentDto commentDto) {
        log.info("Private: Method launched (createComment({}, {}))", userId, commentDto);
        return commentService.createComment(userId, commentDto);
    }

    /**
     * Обновляет существующий комментарий.
     * <p>
     * Позволяет пользователю изменить текст своего комментария. После обновления
     * статус комментария сбрасывается на WAITING для повторной модерации.
     * </p>
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param commentDto DTO с обновленными данными комментария (содержит id и text)
     * @return DTO обновленного комментария
     * @throws ru.practicum.exception.NotFoundException если комментарий с указанным ID не найден
     * @throws ru.practicum.exception.AccessDeniedException если пользователь не является автором комментария
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если переданные данные не проходят валидацию
     * @throws jakarta.validation.ConstraintViolationException если userId не положительный
     */
    @PatchMapping
    public CommentDto updateComment(@PathVariable @Positive long userId,
                                    @RequestBody @Valid UpdateCommentDto commentDto) {
        log.info("Private: Method launched (updateComment({}, {}))", userId, commentDto);
        return commentService.updateComment(userId, commentDto);
    }

    /**
     * Удаляет комментарий.
     * <p>
     * Позволяет пользователю удалить свой собственный комментарий.
     * </p>
     *
     * @param userId идентификатор пользователя (должен быть положительным)
     * @param comId идентификатор удаляемого комментария (должен быть положительным)
     * @throws ru.practicum.exception.NotFoundException если комментарий с указанным ID не найден
     * @throws ru.practicum.exception.AccessDeniedException если пользователь не является автором комментария
     * @throws jakarta.validation.ConstraintViolationException если userId или comId не положительные
     */
    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive long userId,
                              @PathVariable @Positive long comId) {
        log.info("Private: Method launched (deleteComment({}, {}))", userId, comId);
        commentService.deleteComment(userId, comId);
    }
}