package ru.practicum.controller.comment;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.StateCommentDto;
import ru.practicum.model.comment.DateSort;
import ru.practicum.service.comment.CommentService;

import java.util.List;

/**
 * REST-контроллер для административного управления комментариями.
 * <p>
 * Предоставляет endpoints для просмотра всех комментариев с фильтрацией,
 * модерации (подтверждения/отклонения) и удаления комментариев.
 * Доступен только пользователям с ролью администратора.
 * </p>
 *
 * @see CommentService
 * @see StateCommentDto
 * @see DateSort
 */
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCommentController {
    private final CommentService commentService;

    /**
     * Возвращает список всех комментариев с возможностью фильтрации и сортировки.
     * <p>
     * Позволяет администратору просматривать все комментарии в системе с возможностью
     * поиска по тексту и сортировки по дате создания.
     * </p>
     *
     * @param text текст для поиска в комментариях (необязательный параметр)
     * @param sort направление сортировки по дате: ASC - по возрастанию, DESC - по убыванию (по умолчанию ASC)
     * @return список DTO комментариев с информацией о статусе для администратора
     */
    @GetMapping
    public List<StateCommentDto> getComments(
            @RequestParam(required = false) String text,
            @RequestParam(defaultValue = "ASC") DateSort sort
    ) {
        log.info("Admin: Method launched (getComments(text = {}, sort = {}))", text, sort);
        return commentService.getComments(text, sort);
    }

    /**
     * Проводит модерацию комментария: подтверждает или отклоняет его.
     * <p>
     * Позволяет администратору изменить статус комментария. Доступно только для
     * комментариев, находящихся в статусе WAITING.
     * </p>
     *
     * @param comId идентификатор комментария для модерации (должен быть положительным)
     * @param approved true - подтвердить комментарий, false - отклонить
     * @return DTO комментария с обновленным статусом
     * @throws ru.practicum.exception.NotFoundException если комментарий с указанным ID не найден
     * @throws ru.practicum.exception.ValidationException если комментарий не в статусе WAITING
     * @throws jakarta.validation.ConstraintViolationException если comId не положительный
     */
    @PatchMapping("/{comId}")
    public StateCommentDto reviewComment(
            @PathVariable @Positive long comId,
            @RequestParam boolean approved
    ) {
        log.info("Admin: Method launched (reviewComment(comId = {}, approved = {}))", comId, approved);
        return commentService.reviewComment(comId, approved);
    }

    /**
     * Удаляет комментарий.
     * <p>
     * Позволяет администратору удалить любой комментарий из системы независимо от его статуса.
     * </p>
     *
     * @param comId идентификатор удаляемого комментария (должен быть положительным)
     * @throws ru.practicum.exception.NotFoundException если комментарий с указанным ID не найден
     * @throws jakarta.validation.ConstraintViolationException если comId не положительный
     */
    @DeleteMapping("/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive long comId) {
        log.info("Admin: Method launched (deleteComment(comId = {}))", comId);
        commentService.deleteComment(comId);
    }
}